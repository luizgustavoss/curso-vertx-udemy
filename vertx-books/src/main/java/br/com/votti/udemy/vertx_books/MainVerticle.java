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

public class MainVerticle extends AbstractVerticle {

  private InMemoryBookStore memoryStore = new InMemoryBookStore();

  private static final Logger logger =
    LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router = Router.router(vertx);
    router.route()
      .handler(BodyHandler.create())
      .failureHandler(handleFailure());

    handleGetAllBooks(router);
    handleCreateBook(router);
    handleUpdateBook(router);
    handleGetBookByIsbn(router);
    handleDeleteBook(router);

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

      memoryStore.delete(Long.parseLong(isbn));

      req.response()
        .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
        .end();
    }).failureHandler(error -> logger.info("Error {} ", error));
  }

  private void handleGetBookByIsbn(Router router) {
    router.get("/books/:isbn").handler(req -> {
      final String isbn = req.pathParam("isbn");

      var maybeBook = memoryStore.getForId(Long.parseLong(isbn));

      if(!maybeBook.isPresent()){
        req.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject().put("message", "Book not found for isbn: " + isbn).encode());
      }
      else {
        req.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .setStatusCode(HttpResponseStatus.OK.code())
          .end(JsonObject.mapFrom(maybeBook.get()).encode());
      }

    }).failureHandler(error -> logger.info("Error {} ", error));
  }

  private void handleUpdateBook(Router router) {
    router.put("/books/:isbn").handler(req -> {
      final String isbn = req.pathParam("isbn");
      var body = req.getBodyAsJson();
      Book book = memoryStore.update(Long.parseLong(isbn), body.mapTo(Book.class));

      req.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .setStatusCode(HttpResponseStatus.OK.code())
        .end(JsonObject.mapFrom(book).encode());
    }).failureHandler(error -> logger.info("Error {} ", error));
  }

  private void handleCreateBook(Router router) {
    router.post("/books").handler(req -> {
      var body = req.getBodyAsJson();
      memoryStore.add(body.mapTo(Book.class));

      req.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .setStatusCode(HttpResponseStatus.CREATED.code())
        .end(body.encode());
    }).failureHandler(error -> logger.info("Error {} ", error));
  }

  private void handleGetAllBooks(Router router) {
    router.get("/books").handler(req -> {
      req.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(memoryStore.getAll().encode());
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
