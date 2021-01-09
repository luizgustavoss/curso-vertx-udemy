package br.com.votti.udemy.broker.apis.handlers;

import br.com.votti.udemy.broker.db.DBErrorHelper;
import br.com.votti.udemy.broker.model.WatchList;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PutWatchlistFromDatabaseHandler implements Handler<RoutingContext> {

  private final Pool db;

  private static final Logger logger =
    LoggerFactory.getLogger(PutWatchlistFromDatabaseHandler.class);

  public PutWatchlistFromDatabaseHandler(Pool db) {
      this.db = db;
    }

  @Override
  public void handle(RoutingContext context) {

    var accountId = context.pathParam("accountId");
    logger.debug("{} for account {} ", context.normalizedPath(), accountId);

    var json = context.getBodyAsJson();
    var watchList = json.mapTo(WatchList.class);

    var parameterBatch = watchList.getAssets().stream().map(asset -> {
      final Map<String, Object> params = new HashMap<>();
      params.put("account_id", accountId);
      params.put("asset", asset.getName());
      return params;
    }).collect(Collectors.toList());

    db.withTransaction(connection -> {

      return SqlTemplate.forUpdate(connection,
        "delete from watchlist where account_id=#{account_id}")
        .execute(Collections.singletonMap("account_id", accountId))
        .onFailure(DBErrorHelper.errorHandler(context, "Failed to delete watchlist for accountId: " + accountId))
        .compose(deletionDone -> {
          return updateBatch(context, parameterBatch, connection);
        })
        .onFailure(DBErrorHelper.errorHandler(context, "Failed to update into watchlist "))
        .onSuccess(result -> {
          context.response()
            .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
            .end();
        });

    });
  }

  private Future<SqlResult<Void>> updateBatch(RoutingContext context, List<Map<String, Object>> parameterBatch, SqlConnection connection) {
    return SqlTemplate.forUpdate(connection, "insert into watchlist (account_id, asset) values (#{account_id}, #{asset}) on conflict (account_id, asset) do nothing ")
      .executeBatch(parameterBatch)
      .onFailure(DBErrorHelper.errorHandler(context, "Failed to insert into watchlist "));
  }
}
