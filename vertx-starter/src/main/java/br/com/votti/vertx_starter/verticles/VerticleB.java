package br.com.votti.vertx_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleB extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(VerticleAB.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    logger.debug("Start {} ", getClass().getName());
    startPromise.complete();
  }
}
