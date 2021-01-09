package br.com.votti.udemy.broker.apis.handlers;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuoteEntity {

  private String asset;
  private BigDecimal bid;
  private BigDecimal ask;
  @JsonProperty("last_price")
  private BigDecimal lastPrice;
  private BigDecimal volume;

  public JsonObject toJsonObject(){
    return JsonObject.mapFrom(this);
  }
}
