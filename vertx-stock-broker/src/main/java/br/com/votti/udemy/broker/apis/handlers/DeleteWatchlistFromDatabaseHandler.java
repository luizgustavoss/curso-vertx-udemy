package br.com.votti.udemy.broker.apis.handlers;

import br.com.votti.udemy.broker.db.DBErrorHelper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class DeleteWatchlistFromDatabaseHandler implements Handler<RoutingContext> {


  private final Pool db;

  private static final Logger logger =
    LoggerFactory.getLogger(DeleteWatchlistFromDatabaseHandler.class);

  public DeleteWatchlistFromDatabaseHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {

    var accountId = context.pathParam("accountId");
    logger.debug("{} for account {} ", context.normalizedPath(), accountId);

    SqlTemplate.forUpdate(db,
      "delete from watchlist where account_id=#{account_id}")
      .execute(Collections.singletonMap("account_id", accountId))
      .onFailure(DBErrorHelper.errorHandler(context, "Failed to delete watchlist for accountId: " + accountId))
      .onSuccess(result -> {
        context.response()
           .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .end();
      });
  }
}
