package br.com.votti.udemy.broker.apis;

import br.com.votti.udemy.broker.apis.handlers.*;
import br.com.votti.udemy.broker.model.WatchList;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

import java.util.HashMap;
import java.util.UUID;

public class WatchListApi {

  private static final String path = "/account/watchlist/:accountId";
  public static final HashMap<UUID, WatchList> WATCHLIST_PER_ACCOUNT = new HashMap<UUID, WatchList>();

  public static void attach(Router parent, Pool db) {

    parent.get(path).handler(new GetWatchListHandler());
    parent.put(path).handler(new PutWatchListHandler());
    parent.delete(path).handler(new DeleteWatchListHandler());

    parent.get("/pg"+path).handler(new GetWatchlistFromDatabaseHandler(db));
    parent.put("/pg"+path).handler(new PutWatchlistFromDatabaseHandler(db));
    parent.delete("/pg"+path).handler(new DeleteWatchlistFromDatabaseHandler(db));

  }
}
