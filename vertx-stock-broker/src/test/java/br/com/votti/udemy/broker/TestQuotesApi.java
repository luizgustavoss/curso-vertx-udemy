package br.com.votti.udemy.broker;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuotesApi extends AbstractRestApiTest {

  private static final Logger logger =
    LoggerFactory.getLogger(TestQuotesApi.class);

  @Test
  void returns_quote_for_asset(Vertx vertx, VertxTestContext testContext) throws Throwable {

    var client = getClient(vertx);
    client.get("/quotes/AMZN")
      .send()
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        logger.info("Response: {} ", json);

        assertEquals("{\"name\":\"AMZN\"}", json.getJsonObject("asset").encode());
        assertEquals(200, response.statusCode());
        testContext.completeNow();

      }));
  }

  @Test
  void returns_not_found_for_unknown_asset(Vertx vertx, VertxTestContext testContext) throws Throwable {

    var client = getClient(vertx);
    client.get("/quotes/ZAMB")
      .send()
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        logger.info("Response: {} ", json);

        assertEquals("{\"message\":\"quote for asset ZAMB not available!\"}", json.encode());
        assertEquals(404, response.statusCode());
        testContext.completeNow();

      }));
  }
}
