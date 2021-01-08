package br.com.votti.udemy.broker;

import br.com.votti.udemy.broker.model.Asset;
import br.com.votti.udemy.broker.model.WatchList;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestWatchListApi extends AbstractRestApiTest {

  private static final Logger logger =
    LoggerFactory.getLogger(TestWatchListApi.class);

  @Test
  void adds_and_returns_watchlist_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {

    var client = getClient(vertx);

    var accountId = UUID.randomUUID();

    client.put("/account/watchlist/" + accountId.toString())
      .sendJsonObject(
        createBody()
      )
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        logger.info("Response: {} ", json);
        assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
        assertEquals(200, response.statusCode());
      }))
      .compose( next -> {
        client.get("/account/watchlist/" + accountId.toString())
          .send()
          .onComplete(testContext.succeeding(response -> {
            var json = response.bodyAsJsonObject();
            logger.info("Response GET: {} ", json);
            assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
            assertEquals(200, response.statusCode());
          }));
        return Future.succeededFuture();
      }).compose( next -> {
      client.delete("/account/watchlist/" + accountId.toString())
        .send()
        .onComplete(testContext.succeeding(response -> {
          var json = response.bodyAsJsonObject();
          logger.info("Response DELETE: {} ", json);
          assertEquals(204, response.statusCode());
        }));
      return Future.succeededFuture();
    }).compose( next -> {
      client.get("/account/watchlist/" + accountId.toString())
        .send()
        .onComplete(testContext.succeeding(response -> {
          var json = response.bodyAsJsonObject();
          logger.info("Response GET: {} ", json);
          assertEquals(404, response.statusCode());
          testContext.completeNow();
        }));
      return Future.succeededFuture();
    });
  }

  private JsonObject createBody() {
    return new WatchList(Arrays.asList(
      new Asset("AMZN"),
      new Asset("TSLA"))
    ).toJsonObject();
  }

}
