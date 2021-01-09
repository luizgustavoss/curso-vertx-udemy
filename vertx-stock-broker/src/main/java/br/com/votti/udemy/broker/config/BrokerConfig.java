package br.com.votti.udemy.broker.config;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Objects;

@Builder
@Value
@ToString
public class BrokerConfig {

  int serverPort;
  String version;
  DbConfig dbConfig;

  public static BrokerConfig from(final JsonObject config){

    final Integer serverPort = config.getInteger(ConfigLoader.SERVER_PORT);
    if(Objects.isNull(serverPort)){
      throw new RuntimeException(ConfigLoader.SERVER_PORT + " not configured!");
    }

    final String version = config.getString("version");
    if(Objects.isNull(version)){
      throw new RuntimeException("version not configured!");
    }

    return BrokerConfig.builder()
      .serverPort(serverPort)
      .version(version)
      .dbConfig(getDbConfig(config))
      .build();
  }

  private static DbConfig getDbConfig(JsonObject config) {
    final String databaseHost = config.getString("DB_HOST");
    if(Objects.isNull(databaseHost)){
      throw new RuntimeException("database host not configured!");
    }

    final Integer databasePort = config.getInteger("DB_PORT");
    if(Objects.isNull(databasePort)){
      throw new RuntimeException("database port not configured!");
    }

    final String databaseName = config.getString("DB_NAME");
    if(Objects.isNull(databaseName)){
      throw new RuntimeException("database name not configured!");
    }

    final String databaseUsername = config.getString("DB_USERNAME");
    if(Objects.isNull(databaseUsername)){
      throw new RuntimeException("database username not configured!");
    }

    final String databasePassword = config.getString("DB_PASSWORD");
    if(Objects.isNull(databasePassword)){
      throw new RuntimeException("database password not configured!");
    }

    return DbConfig.builder()
      .host(databaseHost)
      .port(databasePort)
      .name(databaseName)
      .username(databaseUsername)
      .password(databasePassword)
      .build();
  }

}
