package br.com.votti.vertx_starter;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ExtendWith(VertxExtension.class)
public class FuturePromiseExampleTest {

  private static final Logger logger = LoggerFactory.getLogger(FuturePromiseExampleTest.class);

  @Test
  void promise_success(Vertx vertx, VertxTestContext context) {

    final Promise<String> promise = Promise.promise();
    logger.debug("Start");
    vertx.setTimer(500, id -> {
      promise.complete("Success");
      logger.debug("Success");
      context.completeNow();
    });
    logger.debug("End");
  }

  @Test
  void promise_failure(Vertx vertx, VertxTestContext context) {

    final Promise<String> promise = Promise.promise();
    logger.debug("Start");
    vertx.setTimer(500, id -> {
      promise.fail(new RuntimeException("Failed!"));
      logger.debug("Failed");
      context.completeNow();
    });
    logger.debug("End");
  }

  @Test
  void future_success(Vertx vertx, VertxTestContext context) {

    final Promise<String> promise = Promise.promise();
    logger.debug("Start");
    vertx.setTimer(500, id -> {
      promise.complete("Success");
      logger.debug("Times done.");
    });
    final Future<String> future = promise.future();
    future
      .onSuccess(result -> {
        logger.debug("Result: {} ", result);
        context.completeNow();
      })
      .onFailure(context::failNow);
  }

  @Test
  void future_failure(Vertx vertx, VertxTestContext context) {

    final Promise<String> promise = Promise.promise();
    logger.debug("Start");
    vertx.setTimer(500, id -> {
      promise.fail(new RuntimeException("Failed!"));
      logger.debug("Times done.");
    });
    final Future<String> future = promise.future();
    future
      .onSuccess(context::failNow)
      .onFailure(error -> {
        logger.debug("Error: {} ", error);
        context.completeNow();
      });
  }

  @Test
  void future_map(Vertx vertx, VertxTestContext context) {

    final Promise<String> promise = Promise.promise();
    logger.debug("Start");
    vertx.setTimer(500, id -> {
      promise.complete("Success");
      logger.debug("Times done.");
    });
    final Future<String> future = promise.future();
    future
      .map(asString -> {
        logger.debug("Map String to JsonObject");
        return new JsonObject().put("key", asString);
      })
      .onSuccess(result -> {
        logger.debug("Result: {} ", result);
        context.completeNow();
      })
      .onFailure(context::failNow);
  }

  @Test
  void future_coordination(Vertx vertx, VertxTestContext context) {
    vertx.createHttpServer()
      .requestHandler( request -> logger.debug("{} ", request))
      .listen(10_000)
      .compose( server-> {
        logger.info("Another task");
        return Future.succeededFuture(server);
      })
      .compose( server-> {
        logger.info("Even Another");
        return Future.succeededFuture(server);
      })
      .onFailure(context::failNow)
      .onSuccess(server -> {
        logger.debug("Server started on port {} ", server.actualPort());
        context.completeNow();
      });
  }

  @Test
  void future_composition(Vertx vertx, VertxTestContext context) {

    var one = Promise.<Void>promise();
    var two = Promise.<Void>promise();
    var three = Promise.<Void>promise();

    var futureOne = one.future();
    var futureTwo = two.future();
    var futureThree = three.future();

    CompositeFuture.all(futureOne, futureTwo, futureThree)
      .onFailure(context::failNow)
      .onSuccess( result -> {
        logger.debug("Success");
        context.completeNow();
      });

    // complete futures
    vertx.setTimer(500, id -> {
      one.complete();
      two.complete();
      three.complete();
    });
  }

}
