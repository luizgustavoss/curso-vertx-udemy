package br.com.votti.udemy.broker;

import br.com.votti.udemy.broker.apis.AssetsApi;
import br.com.votti.udemy.broker.apis.QuotesApi;
import br.com.votti.udemy.broker.apis.WatchListApi;
import br.com.votti.udemy.broker.config.BrokerConfig;
import br.com.votti.udemy.broker.config.ConfigLoader;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger logger =
    LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(configuration -> {
        logger.debug("Retrieved Configuration: {} ", configuration);
        startHttpServerAndAttachRoutes(startPromise, configuration);
      });
  }

  private void startHttpServerAndAttachRoutes(Promise<Void> startPromise,
                                              BrokerConfig configuration) {

    final Router restApi = Router.router(vertx);
    restApi.route()
      .handler(BodyHandler.create())
      .failureHandler(handleFailure());

    AssetsApi.attach(restApi);
    QuotesApi.attach(restApi);
    WatchListApi.attach(restApi);

    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(error-> logger.error("HTTP Server error: {} ", error))
      .listen(configuration.getServerPort(), http -> {

        if (http.succeeded()) {
          startPromise.complete();
          logger.info("HTTP server started on port {}", configuration.getServerPort());
        } else {
          startPromise.fail(http.cause());
        }

      });
  }

  private Handler<RoutingContext> handleFailure() {
    return errorContext -> {
      if(errorContext.response().ended()){
        // could happen if the client cancel the request. Just ignore
        return;
      }
      logger.error("Route error: ", errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong").toBuffer());
    };
  }
}
