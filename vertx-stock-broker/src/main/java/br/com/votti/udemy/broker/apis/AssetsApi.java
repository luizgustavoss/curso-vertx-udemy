package br.com.votti.udemy.broker.apis;

import br.com.votti.udemy.broker.apis.handlers.GetAssetsHandler;
import io.vertx.ext.web.Router;

import java.util.Arrays;
import java.util.List;

public class AssetsApi {

  public static final List<String> ASSETS = Arrays.asList("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA");

  public static void attach(Router parent){
    parent.get("/assets").handler(new GetAssetsHandler());
  }
}
