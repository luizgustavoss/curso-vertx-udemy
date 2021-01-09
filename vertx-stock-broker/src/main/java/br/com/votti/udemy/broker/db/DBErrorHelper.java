package br.com.votti.udemy.broker.db;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBErrorHelper {

  private static final Logger logger =
    LoggerFactory.getLogger(DBErrorHelper.class);

  public static Handler<Throwable> errorHandler(RoutingContext context, String errorMessage) {
    return error -> {
      logger.error("Failure ", error);
      context.response()
        .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
        .end(new JsonObject().put("message", errorMessage).toBuffer());
    };
  }

  public static void notFoundResponse(RoutingContext context, String message) {
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
      .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
      .end(new JsonObject().put("message", message).toBuffer());
  }
}
