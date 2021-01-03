package br.com.votti.vertx_starter.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestResponseExample {

  private static final String BUS_ADDRESS = "br.com.votti.somebus";

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new RequestVerticle());
    vertx.deployVerticle(new ResponseVerticle());
  }

  static class RequestVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(RequestVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      var eventBus = vertx.eventBus();
      eventBus.<String>request(BUS_ADDRESS, "Hello World", reply -> {
        logger.debug("Response: {} ", reply.result().body());
      });
    }
  }

  static class ResponseVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ResponseVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().<String>consumer(BUS_ADDRESS, message -> {
        logger.debug("Received: {} ", message.body());
        message.reply("Received your message ");
      });
    }
  }
}
