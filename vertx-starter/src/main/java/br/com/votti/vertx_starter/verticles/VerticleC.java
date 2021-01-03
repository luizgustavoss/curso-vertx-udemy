package br.com.votti.vertx_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleC extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(VerticleAB.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    logger.debug("Start  {}  on thread {}  with config {}",
      getClass().getName(),
      Thread.currentThread().getName(),
      config().toString());
    startPromise.complete();
  }
}
