package emodroid.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.caucho.resin.HttpEmbed;
import com.caucho.resin.ResinEmbed;
import com.caucho.resin.WebAppEmbed;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;

public class TestTest {

	private ResinEmbed server = null;

	private void startServer() {
		if (server != null) {
			throw new IllegalStateException("Server is already started");
		}

		server = new ResinEmbed();

		HttpEmbed http = new HttpEmbed(8080);
		server.addPort(http);

		WebAppEmbed webApp = new WebAppEmbed("/", "src/main/web");
		server.addWebApp(webApp);

		server.start();
	}

	private void stopServer() {
		server.stop();
	}

	@Test
	public void runAnEmbeddedResin() throws InterruptedException, IOException {
		startServer();
		String result = server.request("GET /index.html");
		assertTrue("an index is set up with a welcome page",
				result.contains("SMS Over IP - Welcome!"));
		String smokeResult = server.request("GET /smoke");
		assertTrue("smoke test servlet works as expected",
				smokeResult.contains("smoke test for SMS over IP"));
		stopServer();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void runADisruptor() {
		final ExecutorService executor = Executors.newCachedThreadPool();
		
		Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(
				ValueEvent.EVENT_FACTORY, executor,
				new SingleThreadedClaimStrategy(1024),
				new SleepingWaitStrategy());
		
		final Counter counter = new Counter();
		
		disruptor.handleEventsWith(new EventHandler<ValueEvent>() {
			public void onEvent(final ValueEvent event, final long sequence,
					final boolean endOfBatch) throws Exception {
				counter.inc();
			}
		});
		
		RingBuffer<ValueEvent> ringBuffer = disruptor.start();
		
		long seq = ringBuffer.next();
		final ValueEvent ev1 = ringBuffer.get(seq);
		ev1.setValue(345);
		ringBuffer.publish(seq);
		
		seq = ringBuffer.next();
		final ValueEvent ev2 = ringBuffer.get(seq);
		ev2.setValue(346);
		ringBuffer.publish(seq);
		
		disruptor.shutdown();
		executor.shutdownNow();
		
		assertEquals(2, counter.count());
	}

	private static final class Counter {
		private long count = 0;
		
		public void inc() {
			count++;
		}
		
		public long count() {
			return count;
		}
	}
	
	private static final class ValueEvent {
		private long value;

		public long getValue() {
			return value;
		}

		public void setValue(final long value) {
			this.value = value;
		}

		public final static EventFactory<ValueEvent> EVENT_FACTORY = new EventFactory<ValueEvent>() {
			public ValueEvent newInstance() {
				return new ValueEvent();
			}
		};
	}

}
