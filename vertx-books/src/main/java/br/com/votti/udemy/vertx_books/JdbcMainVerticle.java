package br.com.votti.udemy.vertx_books;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcMainVerticle extends AbstractVerticle {

  private JdbcBookRepository repository;

  private static final Logger logger =
    LoggerFactory.getLogger(JdbcMainVerticle.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new JdbcMainVerticle());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    repository = new JdbcBookRepository(vertx);

    Router router = Router.router(vertx);
    router.route()
      .handler(BodyHandler.create())
      .failureHandler(handleFailure());

    handleGetAllBooks(router);
    handleCreateBook(router);
    handleDeleteBook(router);
    handleUpdateBook(router);
    handleGetBookByIsbn(router);

    vertx.createHttpServer().requestHandler(router)
      .listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        logger.info("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private void handleDeleteBook(Router router) {
    router.delete("/books/:isbn").handler(req -> {
      final String isbn = req.pathParam("isbn");

      repository.delete(Long.parseLong(isbn)).setHandler(ar -> {
        if (ar.failed()) {
          req.fail(ar.cause());
          return;
        }
        req.response()
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .end();
      });
    });
  }

  private void handleGetBookByIsbn(Router router) {
    router.get("/books/:isbn").handler(req -> {
      final String isbn = req.pathParam("isbn");

      repository.getForId(Long.parseLong(isbn)).setHandler( ar -> {
        if (ar.failed()) {
          req.fail(ar.cause());
          return;
        }
        req.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .setStatusCode(HttpResponseStatus.OK.code())
          .end(JsonObject.mapFrom(ar.result()).encode());
      });
    });
  }

  private void handleUpdateBook(Router router) {
    router.put("/books/:isbn").handler(req -> {
      final String isbn = req.pathParam("isbn");
      var body = req.getBodyAsJson();
      repository.update(Long.parseLong(isbn), body.mapTo(Book.class)).setHandler( ar -> {
        if (ar.failed()) {
          req.fail(ar.cause());
          return;
        }
        req.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .setStatusCode(HttpResponseStatus.OK.code())
          .end();
      });
    });
  }

  private void handleCreateBook(Router router) {
    router.post("/books").handler(req -> {
      var body = req.getBodyAsJson();
      repository.add(body.mapTo(Book.class)).setHandler( ar -> {
        if(ar.failed()){
          req.fail(ar.cause());
          return;
        }
        req.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .setStatusCode(HttpResponseStatus.CREATED.code())
          .end(body.encode());
      });
    });
  }


  private void handleGetAllBooks(Router router) {
    router.get("/books").handler(req -> {

      repository.getAll().setHandler(ar -> {
        if(ar.failed()){
          req.fail(ar.cause());
          return;
        }
        req.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(ar.result().encode());
      });
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
        .end(new JsonObject().put("message", errorContext.failure().getMessage()).toBuffer());
    };
  }
}
