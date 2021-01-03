package br.com.votti.vertx_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleAA extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(VerticleAA.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    logger.debug("Start {} ", getClass().getName());
    startPromise.complete();
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    logger.debug("Stop {} ", getClass().getName());
    stopPromise.complete();
  }
}
