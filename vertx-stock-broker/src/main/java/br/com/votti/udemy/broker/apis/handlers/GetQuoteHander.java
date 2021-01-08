package br.com.votti.udemy.broker.apis.handlers;

import br.com.votti.udemy.broker.apis.QuotesApi;
import br.com.votti.udemy.broker.model.Quote;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class GetQuoteHander implements Handler<RoutingContext> {

  private static final Logger logger =
    LoggerFactory.getLogger(GetQuoteHander.class);

  private Map<String, Quote> cachedQuotes;

  public GetQuoteHander(Map<String, Quote> cachedQuotes) {
    this.cachedQuotes = cachedQuotes;
  }

  @Override
  public void handle(RoutingContext context) {
      final String assetParam = context.pathParam("asset");
      logger.debug("asset: {}", assetParam);

      var maybeQuote = Optional.ofNullable(cachedQuotes.get(assetParam));
      if(maybeQuote.isEmpty()) {
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject().put("message", "quote for asset " + assetParam + " not available!").toBuffer());
        return;
      }

      final JsonObject response = maybeQuote.get().toJsonObject();

      logger.info("Path {} responds with {} ", context.normalizedPath(), response.encode());
      context.response()
        .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
        .end(response.toBuffer());
  }
}
