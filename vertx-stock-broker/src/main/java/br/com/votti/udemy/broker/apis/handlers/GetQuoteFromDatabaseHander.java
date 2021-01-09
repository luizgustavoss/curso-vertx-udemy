package br.com.votti.udemy.broker.apis.handlers;

import br.com.votti.udemy.broker.db.DBErrorHelper;
import br.com.votti.udemy.broker.model.Quote;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class GetQuoteFromDatabaseHander implements Handler<RoutingContext> {

  private final Pool db;

  private static final Logger logger =
    LoggerFactory.getLogger(GetQuoteHander.class);

  public GetQuoteFromDatabaseHander(Map<String, Quote> cachedQuotes, Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {

    final String assetParam = context.pathParam("asset");
    logger.debug("asset: {}", assetParam);

    SqlTemplate.forQuery(db,
      "select q.asset, q.bid, q.ask, q.last_price, q.volume from public.quotes q where q.asset=#{asset}")
      .mapTo(QuoteEntity.class)
      .execute(Collections.singletonMap("asset", assetParam))
      .onFailure(error -> {
        DBErrorHelper.errorHandler(context, "Failed to get quotes for asset " + assetParam + " from database!");
      })
      .onSuccess(quotes -> {
        if(!quotes.iterator().hasNext()){
          DBErrorHelper.notFoundResponse(context, "quote for asset " + assetParam + " not available!");
        } else {
          var response = quotes.iterator().next().toJsonObject();

          logger.info("Path {} responds with {} ", context.normalizedPath(), response.encode());
          context.response()
            .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
            .end(response.toBuffer());
        }
      });
  }


}
