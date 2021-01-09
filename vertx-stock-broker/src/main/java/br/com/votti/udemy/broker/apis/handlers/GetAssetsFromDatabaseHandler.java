package br.com.votti.udemy.broker.apis.handlers;

import br.com.votti.udemy.broker.db.DBErrorHelper;
import br.com.votti.udemy.broker.model.Asset;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAssetsFromDatabaseHandler implements Handler<RoutingContext> {

  private final Pool db;

  private static final Logger logger = LoggerFactory.getLogger(GetAssetsFromDatabaseHandler.class);


  public GetAssetsFromDatabaseHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {


    db.query("select a.value from public.assets a")
      .execute()
      .onFailure(error -> {
        DBErrorHelper.errorHandler(context, "Failed to get assets from database!");
      })
      .onSuccess(result -> {
        var response = new JsonArray();
        result.forEach(row -> {
          response.add(new Asset(row.getValue("value").toString()));
        });
        logger.info("Path {} responds with {} ",
          context.normalizedPath(), response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
          .end(response.toBuffer());
      });
  }
}
