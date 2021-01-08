package br.com.votti.udemy.broker;

import br.com.votti.udemy.broker.config.ConfigLoader;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionInfoVerticle extends AbstractVerticle {

  private static final Logger logger =
    LoggerFactory.getLogger(VersionInfoVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(configuration -> {
        logger.debug("Current Application Version is: {} ", configuration.getVersion());
        startPromise.complete();
      });
  }

}
