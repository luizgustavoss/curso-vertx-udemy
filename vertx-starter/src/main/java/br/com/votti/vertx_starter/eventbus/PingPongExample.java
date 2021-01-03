package br.com.votti.vertx_starter.eventbus;

import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PingPongExample {

  private static final String BUS_ADDRESS = PingVerticle.class.getName();
  private static final Logger logger = LoggerFactory.getLogger(PingPongExample.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new PingVerticle(), logOnError());
    vertx.deployVerticle(new PongVerticle(), logOnError());
  }

  private static Handler<AsyncResult<String>> logOnError() {
    return ar -> {
      if (ar.failed()) {
        logger.error("Error: ", ar.cause());
      }
    };
  }

  static class PingVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(PingVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

      final Ping ping = new Ping("Ping", true);

      vertx.eventBus().registerDefaultCodec(Ping.class, new LocalMessageCodec<>(Ping.class));
      vertx.eventBus().<Pong>request(BUS_ADDRESS, ping, reply -> {
        if(reply.failed()){
          logger.error("Error: ", reply.cause());
          return;
        }

        logger.debug("Response: {} ", reply.result().body());
      });
      startPromise.complete();
    }
  }

  static class PongVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(PongVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

      final Pong pong = new Pong("Pong", true);

      vertx.eventBus().registerDefaultCodec(Pong.class, new LocalMessageCodec<>(Pong.class));
      vertx.eventBus().<Ping>consumer(BUS_ADDRESS, message -> {
        logger.debug("Received: {} ", message.body());
        message.reply(pong);
      }).exceptionHandler(error -> {
        logger.error("Error: ", error.getMessage());
      });
      startPromise.complete();
    }
  }
}
