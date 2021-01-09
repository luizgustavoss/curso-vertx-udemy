package br.com.votti.udemy.broker.apis;

import br.com.votti.udemy.broker.apis.handlers.GetQuoteFromDatabaseHander;
import br.com.votti.udemy.broker.apis.handlers.GetQuoteHander;
import br.com.votti.udemy.broker.model.Asset;
import br.com.votti.udemy.broker.model.Quote;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesApi {

  private final static Map<String, Quote> cachedQuotes = new HashMap<>();

  public static void attach(Router parent, Pool db){

    AssetsApi.ASSETS.forEach(symbol -> {
      cachedQuotes.put(symbol, initRandomQuote(symbol));
    });

    parent.get("/quotes/:asset").handler(new GetQuoteHander(cachedQuotes));
    parent.get("/pg/quotes/:asset").handler(new GetQuoteFromDatabaseHander(cachedQuotes, db));
  }

  private static Quote initRandomQuote(String assetParam) {
    return Quote.builder()
      .ask(randomValue())
      .bid(randomValue())
      .volume(randomValue())
      .lastPrice(randomValue())
      .asset(new Asset(assetParam))
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(
      ThreadLocalRandom.current().nextDouble(1, 100));
  }
}
