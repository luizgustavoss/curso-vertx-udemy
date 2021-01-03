package br.com.votti.vertx_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleA extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(VerticleA.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    logger.debug("Start " + getClass().getName());
    vertx.deployVerticle(new VerticleAA(), whenDeployed -> {
      logger.debug("Deployed {} ", VerticleAA.class.getName());
      vertx.undeploy(whenDeployed.result());
    });
    vertx.deployVerticle(new VerticleAB(), whenDeployed -> {
      logger.debug("Deployed {} ", VerticleAB.class.getName());
    });
    startPromise.complete();
  }
}
