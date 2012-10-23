package emodroid.integration;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.junit.Test;

import com.caucho.hessian.client.HessianProxyFactory;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.MetricName;

import emodroid.api.SMSAPI;
import emodroid.utils.CanEmbedAServer;

public class PerformanceTest extends CanEmbedAServer {

	@Test
	public void shouldHandle10000SMSInASecond() throws IOException,
			InterruptedException, ExecutionException {
		startServer();

		// Warm-Up
		final Histogram warmUpRestHisto = Metrics.newHistogram(new MetricName(
				PerformanceTest.class, "h-rest-warmup")); 

		final Histogram warmUpHessianHisto = Metrics.newHistogram(new MetricName(
				PerformanceTest.class, "h-hessian-warmup")); 

		new RunRestPushSMSCalls(warmUpRestHisto).run();

		printHistogram("WARM-UP REST", warmUpRestHisto);
		
		new RunHessianPushSMSCalls(warmUpHessianHisto).run();
		
		printHistogram("WARM-UP Hessian", warmUpHessianHisto);
		
		// Perform
		runRestPerf();
		runHessianPerf();
		
		stopServer();
	}
	
	private void runRestPerf() throws InterruptedException, ExecutionException {
		final Histogram h = Metrics.newHistogram(new MetricName(
				PerformanceTest.class, "h-rest-perf"));

		ExecutorService executor = Executors.newFixedThreadPool(20);

		final List<Future<?>> runs = new LinkedList<Future<?>>();

		for (int i = 0; i < 10; ++i) {
			runs.add(executor.submit(new RunRestPushSMSCalls(h)));
		}

		for (final Future<?> run : runs) {
			run.get();
		}

		printHistogram("REST", h);
		
		executor.shutdown();		
	}

	private void printHistogram(final String id, final Histogram h) {
		System.out.println(id + " results : ");
		System.out.println("min = " + nanoToMSec(h.min()) + "; max = " + nanoToMSec(h.max())
				+ "; mean = " + nanoToMSec(h.mean()) + "; std dev = " + h.stdDev() + "; count = " + h.count() + "; cumulated time = " + nanoToMSec(h.sum()));
	}
	
	private void runHessianPerf() throws InterruptedException, ExecutionException {
		final Histogram h = Metrics.newHistogram(new MetricName(
				PerformanceTest.class, "h-hessian-perf"));

		ExecutorService executor = Executors.newFixedThreadPool(20);

		final List<Future<?>> runs = new LinkedList<Future<?>>();

		for (int i = 0; i < 10; ++i) {
			runs.add(executor.submit(new RunHessianPushSMSCalls(h)));
		}

		for (final Future<?> run : runs) {
			run.get();
		}

		printHistogram("Hessian", h);

		executor.shutdown();		
	}

	private static double nanoToMSec(final double nano) {
		return nano / 1000000.0;
	}
	
	private static class RunRestPushSMSCalls implements Runnable {

		private final Histogram h;

		public RunRestPushSMSCalls(final Histogram h) {
			this.h = h;
		}

		@Override
		public void run() {
			final JSONObject json = new JSONObject();
			final JSONArray to = new JSONArray();

			to.add(UUID.randomUUID().toString());
			to.add(UUID.randomUUID().toString());
			json.put("to", to);

			final int max_iterations = 100;

			final long[] responses = new long[max_iterations];

			for (int i = 0; i < max_iterations; ++i) {
				json.put("content", "This is a text message "
						+ UUID.randomUUID().toString());

				final long triggered = System.nanoTime();

				given().content(json.toJSONString()).expect()
						.body(equalTo("sent")).when().post("/sms");

				responses[i] = System.nanoTime() - triggered;
			}

			for (int i = 0; i < max_iterations; ++i) {
				synchronized (h) {
					h.update(responses[i]);
				}
			}
		}
	}


	private static class RunHessianPushSMSCalls implements Runnable {

		private final Histogram h;

		public RunHessianPushSMSCalls(final Histogram h) {
			this.h = h;
		}

		@Override
		public void run() {
			final String url = "http://localhost:8080/hessian/sms";

			final HessianProxyFactory factory = new HessianProxyFactory();
			SMSAPI sms = null;
			try {
				sms = (SMSAPI) factory.create(SMSAPI.class, url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
	
			final int max_iterations = 300;

			final long[] responses = new long[max_iterations];

			for (int i = 0; i < max_iterations; ++i) {
				final long triggered = System.nanoTime();

				sms.push(new String[] {UUID.randomUUID().toString(), UUID.randomUUID().toString()}, "This is a text message " + UUID.randomUUID().toString());
		
				responses[i] = System.nanoTime() - triggered;
			}

			for (int i = 0; i < max_iterations; ++i) {
				synchronized (h) {
					h.update(responses[i]);
				}
			}
		}
	}

}
