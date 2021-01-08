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

import java.util.concurrent.ThreadLocalRandom;

public class GetAssetsHandler implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(GetAssetsHandler.class);

  @Override
  public void handle(RoutingContext context) {

    final JsonArray response = new JsonArray();

    // force a performance issue to allocate other threads
    try {
      Thread.sleep(ThreadLocalRandom.current().nextInt(100, 300));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    AssetsApi.ASSETS.stream().map(Asset::new).forEach(response::add);
    logger.info("Path {} responds with {} ", context.normalizedPath(), response.encode());
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
      .end(response.toBuffer());
  }
}
