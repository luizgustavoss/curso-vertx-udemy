package br.com.votti.vertx_starter.worker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(WorkerVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    logger.debug("Deployed as worker verticle");
    startPromise.complete();
    Thread.sleep(5000);
  }
}
