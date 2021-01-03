package br.com.votti.vertx_starter.eventloops;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class EventLoopExample extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(EventLoopExample.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx(
      new VertxOptions()
        .setMaxEventLoopExecuteTime(500)
        .setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS)
        .setBlockedThreadCheckInterval(1)
        .setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS)
    );

    vertx.deployVerticle(new EventLoopExample());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    logger.debug("Sart {} ", getClass().getName());
    startPromise.complete();
    Thread.sleep(5000);
  }
}
