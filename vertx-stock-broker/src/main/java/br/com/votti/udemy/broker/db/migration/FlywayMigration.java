package br.com.votti.udemy.broker.db.migration;

import br.com.votti.udemy.broker.config.DbConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlywayMigration {

  private static final Logger logger =
    LoggerFactory.getLogger(FlywayMigration.class);

  public static Future<Void> migrate(Vertx vertx, DbConfig dbConfig) {

    return vertx.<Void>executeBlocking(promisse -> {
      execute(dbConfig);
      promisse.complete();
    }).onFailure( error -> logger.error("Failed to migrate db schema with error: ", error));
  }

  private static void execute(DbConfig dbConfig) {
    final  String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
      dbConfig.getHost(),
      dbConfig.getPort(),
      dbConfig.getName());

    logger.debug("Migrating DB schema using jdbc url: {}", jdbcUrl);

    final Flyway flyway = Flyway.configure()
      .dataSource(jdbcUrl, dbConfig.getUsername(), dbConfig.getPassword())
      .schemas("public")
      .defaultSchema("public")
      .load();

    var current = Optional.ofNullable(flyway.info().current());
    current.ifPresent( info -> logger.info("db schema is at version: {}", info.getVersion()));

    var pendingMigrations = flyway.info().pending();
    logger.debug("Pending migrations are: {}", printMigrations(pendingMigrations));

    flyway.migrate();
  }

  private static String printMigrations(MigrationInfo[] pending) {
    if(Objects.isNull(pending)){
      return "[]";
    }
    return Arrays.stream(pending)
      .map(each -> each.getVersion() + " - " + each.getDescription())
      .collect(Collectors.joining(",", "[", "]"));
  }
}
