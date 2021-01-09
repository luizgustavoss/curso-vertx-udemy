package br.com.votti.udemy.broker.apis.handlers;

import br.com.votti.udemy.broker.db.DBErrorHelper;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class GetWatchlistFromDatabaseHandler implements Handler<RoutingContext> {

  private final Pool db;

  private static final Logger logger =
    LoggerFactory.getLogger(GetWatchlistFromDatabaseHandler.class);

  public GetWatchlistFromDatabaseHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {

    var accountId = context.pathParam("accountId");
    logger.debug("{} for account {} ", context.normalizedPath(), accountId);

    SqlTemplate.forQuery(db,
      "select w.asset from watchlist w where w.account_id=#{account_id}")
      .mapTo(Row::toJson)
      .execute(Collections.singletonMap("account_id",accountId))
      .onFailure(DBErrorHelper.errorHandler(context, "Failed to fetch watchlist for accountId: " + accountId))
      .onSuccess(assets -> {
        if(!assets.iterator().hasNext()){
          DBErrorHelper.notFoundResponse(context, "watchlist for account " + accountId + " not available!");
          return;
        }

        var response = new JsonArray();
        assets.forEach(response::add);

        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
          .setStatusCode(HttpResponseStatus.OK.code())
          .end(response.toBuffer());

      });





  }
}
