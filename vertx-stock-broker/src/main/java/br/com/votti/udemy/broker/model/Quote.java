package br.com.votti.udemy.broker.model;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class Quote {

  private Asset asset;
  private BigDecimal bid;
  private BigDecimal ask;
  private BigDecimal lastPrice;
  private BigDecimal volume;


  public JsonObject toJsonObject(){
    return JsonObject.mapFrom(this);
  }
}
