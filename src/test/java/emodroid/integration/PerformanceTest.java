package emodroid.integration;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.junit.Test;

import com.jayway.restassured.response.Response;

import emodroid.utils.CanEmbedAServer;

public class PerformanceTest extends CanEmbedAServer {

	@Test
	public void shouldHandle10000SMSInASecond() throws IOException, InterruptedException, ExecutionException {
		startServer();

		final JSONObject json = new JSONObject();
		final JSONArray to = new JSONArray();
		to.add("recipient1");
		to.add("recipient2");
		json.put("to", to);

		final long triggered = System.currentTimeMillis();

		ExecutorService executor = Executors.newFixedThreadPool(20);

		final List<Future<Response>> responses = new LinkedList<Future<Response>>();

		for (int i = 0; i < 1000; ++i) {
			json.put("content", "This is a text message "
					+ UUID.randomUUID().toString());

			responses.add(executor.submit(new Callable<Response>() {

				@Override
				public Response call() throws Exception {
					return given().content(json.toJSONString()).expect()
							.body(equalTo("sent")).when().post("/sms");
				}

			}));

		}

		for(final Future<Response> r : responses) {
			r.get();
		}
		
		assertTrue("run 10000 sms delivery in less than a second",
				System.currentTimeMillis() - triggered <= 1000);

		stopServer();
	}

}
