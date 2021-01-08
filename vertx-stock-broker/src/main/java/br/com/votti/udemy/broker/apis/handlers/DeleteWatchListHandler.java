package br.com.votti.udemy.broker.apis.handlers;

import br.com.votti.udemy.broker.apis.WatchListApi;
import br.com.votti.udemy.broker.model.WatchList;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class DeleteWatchListHandler implements Handler<RoutingContext> {

  private static final Logger logger =
    LoggerFactory.getLogger(DeleteWatchListHandler.class);

  @Override
  public void handle(RoutingContext context) {
    var accountId = context.pathParam("accountId");
    logger.debug("{} for account {} ", context.normalizedPath(), accountId);

    WatchListApi.WATCHLIST_PER_ACCOUNT.remove(UUID.fromString(accountId));

    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
      .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
      .end();
  }
}
