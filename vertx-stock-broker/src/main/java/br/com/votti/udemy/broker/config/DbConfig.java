package br.com.votti.udemy.broker.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DbConfig {

  private String host;
  private Integer port;
  private String name;
  private String username;
  private String password;

  @Override
  public String toString() {
    return "DbConfig{" +
      "host='" + host + '\'' +
      ", port=" + port +
      ", name='" + name + '\'' +
      ", username='" + username + '\'' +
      ", password='******'" +
      '}';
  }
}
