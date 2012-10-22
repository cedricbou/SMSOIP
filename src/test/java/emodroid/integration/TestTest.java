package emodroid.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;

import emodroid.utils.CanEmbedAServer;

public class TestTest extends CanEmbedAServer {

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
	public void runADisruptor() throws InterruptedException {
		final ExecutorService executor = Executors.newCachedThreadPool();
		
		Disruptor<Command> disruptor = new Disruptor<Command>(
				Command.EVENT_FACTORY, executor,
				new SingleThreadedClaimStrategy(1024),
				new SleepingWaitStrategy());
		

		final Journaller journaller1 = new Journaller();
		final Journaller journaller2 = new Journaller();
		final Journaller journaller3 = new Journaller();
		final Counter counter = new Counter();
		
		disruptor.handleEventsWith(journaller1, journaller2, journaller3).then(counter);
		disruptor.start();
		
		final CommandTranslator randomCommands = new CommandTranslator();
		
		for(int i = 0; i < 20; ++i) {
			disruptor.publishEvent(randomCommands);
			Thread.sleep(5);
		}
		
		disruptor.shutdown();
		executor.shutdownNow();
		
		assertEquals(20, counter.count());
	}

	private static final class Command {
		private long value;

		public long getValue() {
			return value;
		}

		public void setValue(final long value) {
			this.value = value;
		}

		public final static EventFactory<Command> EVENT_FACTORY = new EventFactory<Command>() {
			public Command newInstance() {
				return new Command();
			}
		};
	}

	private static final class Journaller implements EventHandler<Command> {
		@Override
		public void onEvent(Command event, long sequence, boolean endOfBatch)
				throws Exception {
			System.out.println("Got command : " + event.getValue() + " on thread " + Thread.currentThread().getName());
		}
	}
	
	private static final class Counter implements EventHandler<Command> {
		private long count = 0;
		
		@Override
		public void onEvent(Command event, long sequence, boolean endOfBatch)
				throws Exception {
			count++;
		}
		
		public long count() {
			return count;
		}
	}
	
	private static final class CommandTranslator implements EventTranslator<Command> {
		long commandId = 0;
		
		@Override
		public void translateTo(Command event, long sequence) {
			event.setValue(commandId++);
		}
	}
}
