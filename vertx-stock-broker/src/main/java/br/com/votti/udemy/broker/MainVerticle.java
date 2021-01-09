package br.com.votti.udemy.broker;

import br.com.votti.udemy.broker.config.ConfigLoader;
import br.com.votti.udemy.broker.db.migration.FlywayMigration;
import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger logger =
    LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> {
      logger.error("Unhandled error: {} ", error);
    });
    vertx.deployVerticle(MainVerticle.class.getName())
      .onFailure(err -> logger.error("Failed to deploy: {}", err))
      .onSuccess( id -> {
        logger.info("Deployed {} with ID {} ", MainVerticle.class.getSimpleName(), id);
      });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    vertx.deployVerticle(VersionInfoVerticle.class.getName())
      .onFailure(startPromise::fail)
      .onSuccess(id -> logger.info("Deployed {} with ID {} ", VersionInfoVerticle.class.getSimpleName(), id))
      .compose(next -> migrateDatabase())
      .onFailure(startPromise::fail)
      .onSuccess(id -> logger.info("Migrated DB schema to latest version!"))
      .compose(next -> deployRestApiVerticle(startPromise)
      );
  }

  private Future<Void> migrateDatabase() {

    return ConfigLoader.load(vertx)
      .compose(config -> FlywayMigration.migrate(vertx, config.getDbConfig()));
  }

  private Future<String> deployRestApiVerticle(Promise<Void> startPromise) {
    return vertx.deployVerticle(RestApiVerticle.class.getName(),
      new DeploymentOptions().setInstances(getProcessors()))
      .onFailure(startPromise::fail)
      .onSuccess(id -> {
        logger.info("Deployed {} with ID {} ", RestApiVerticle.class.getSimpleName(), id);
        startPromise.complete();
      });
  }

  private int getProcessors() {
    return Math.max(1, Runtime.getRuntime().availableProcessors());
  }

}
