package br.com.votti.udemy.broker.apis;

import br.com.votti.udemy.broker.apis.handlers.DeleteWatchListHandler;
import br.com.votti.udemy.broker.apis.handlers.GetWatchListHandler;
import br.com.votti.udemy.broker.apis.handlers.PutWatchListHandler;
import br.com.votti.udemy.broker.model.WatchList;
import io.vertx.ext.web.Router;

import java.util.HashMap;
import java.util.UUID;

public class WatchListApi {

  private static final String path = "/account/watchlist/:accountId";
  public static final HashMap<UUID, WatchList> WATCHLIST_PER_ACCOUNT = new HashMap<UUID, WatchList>();

  public static void attach(Router parent) {

    parent.get(path).handler(new GetWatchListHandler());
    parent.put(path).handler(new PutWatchListHandler());
    parent.delete(path).handler(new DeleteWatchListHandler());
  }
}
