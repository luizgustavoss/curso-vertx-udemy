package br.com.votti.udemy.broker.apis.handlers;

import br.com.votti.udemy.broker.apis.WatchListApi;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class GetWatchListHandler implements Handler<RoutingContext> {

  private static final Logger logger =
    LoggerFactory.getLogger(GetWatchListHandler.class);

  @Override
  public void handle(RoutingContext context) {

      var accountId = context.pathParam("accountId");
      logger.debug("{} for account {} ", context.normalizedPath(), accountId);
      var watchList = Optional.ofNullable(WatchListApi.WATCHLIST_PER_ACCOUNT.get(UUID.fromString(accountId)));

      if(watchList.isEmpty()){
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject().put("message", "watchlist for account " + accountId + " not available!").toBuffer());
        return;
      }

      context.response()
        .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
        .setStatusCode(HttpResponseStatus.OK.code())
        .end(watchList.get().toJsonObject().toBuffer());

    }
}
