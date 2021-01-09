package br.com.votti.udemy.broker.apis.handlers;

import br.com.votti.udemy.broker.apis.AssetsApi;
import br.com.votti.udemy.broker.model.Asset;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAssetsHandler implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(GetAssetsHandler.class);

  @Override
  public void handle(RoutingContext context) {

    final JsonArray response = new JsonArray();

    AssetsApi.ASSETS.stream().map(Asset::new).forEach(response::add);
    logger.info("Path {} responds with {} ", context.normalizedPath(), response.encode());
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
      .end(response.toBuffer());
  }
}
