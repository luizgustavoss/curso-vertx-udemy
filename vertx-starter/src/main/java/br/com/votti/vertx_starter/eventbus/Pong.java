package br.com.votti.vertx_starter.eventbus;

public class Pong {

  private String message;
  private boolean enabled;

  public Pong(String message, boolean enabled) {
    this();
    this.message = message;
    this.enabled = enabled;
  }

  public Pong() {
  }

  public String getMessage() {
    return message;
  }

  public boolean isEnabled() {
    return enabled;
  }


  @Override
  public String toString() {
    return "Pong{" +
      "message='" + message + '\'' +
      ", enabled=" + enabled +
      '}';
  }
}
