package br.com.votti.udemy.broker.apis;

import br.com.votti.udemy.broker.apis.handlers.GetAssetsFromDatabaseHandler;
import br.com.votti.udemy.broker.apis.handlers.GetAssetsHandler;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

import java.util.Arrays;
import java.util.List;

public class AssetsApi {

  public static final List<String> ASSETS = Arrays.asList("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA");

  public static void attach(Router parent, final Pool db){
    parent.get("/assets").handler(new GetAssetsHandler());
    parent.get("/pg/assets").handler(new GetAssetsFromDatabaseHandler(db));
  }
}
