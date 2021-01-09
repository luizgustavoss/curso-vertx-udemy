package br.com.votti.udemy.vertx_books;


import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;

import java.util.List;

public class JdbcBookRepository {

  private SQLClient sql;

  public JdbcBookRepository(final Vertx vertx){

    final JsonObject config = new JsonObject();
    config.put("url", "jdbc:postgresql://localhost:5432/vertx-books");
    config.put("driver_class", "org.postgresql.Driver");
    config.put("user", "postgres");
    config.put("password", "postgres");

    sql = JDBCClient.createShared(vertx, config);
  }

  public Future<JsonArray> getAll() {
    final Future<JsonArray> result = Future.future();
    sql.query("select * from books", ar -> {
      if(ar.failed()){
        result.fail(ar.cause());
        return;
      } else {
        final List<JsonObject> rows = ar.result().getRows();
        final JsonArray res = new JsonArray();
        rows.forEach(res::add);
        result.complete(res);
      }
    });
    return result;
  }

  public Future<Void> add(final Book book) {
    final Future<Void> result = Future.future();
    JsonArray params = new JsonArray();
    params.add(book.getIsbn());
    params.add(book.getTitle());

    sql.updateWithParams("insert into books (isbn, title) values (?, ?)",
      params, ar -> {
      if(ar.failed()){
        result.fail(ar.cause());
        return;
      }

      if(ar.result().getUpdated() != 1){
        result.fail(new IllegalStateException("wrong update count on insert" + ar.result()));
        return;
      }
      result.complete();
    });
    return result;
  }

  public Future<Void> delete(Long isbn) {
    final Future<Void> result = Future.future();
    JsonArray params = new JsonArray();
    params.add(isbn);

    sql.updateWithParams("delete from books where isbn = ?",
      params, ar -> {
        if(ar.failed()){
          result.fail(ar.cause());
          return;
        }
        result.complete();
      });
    return result;
  }

  public Future<JsonObject> getForId(long isbn) {
    final Future<JsonObject> result = Future.future();
    JsonArray params = new JsonArray();
    params.add(isbn);

    sql.queryWithParams("select * from books where isbn = ?",
      params, ar -> {
      if(ar.failed()){
        result.fail(ar.cause());
        return;
      }
      if(ar.result().getNumRows() == 0){
        result.fail(new IllegalArgumentException("No book found for isbn " + isbn));
        return;
      }
      final JsonObject resultBook = ar.result().getRows().get(0);
      result.complete(resultBook);
    });
    return result;
  }

  public Future<Void> update(Long isbn, Book book) {
    final Future<Void> result = Future.future();
    JsonArray params = new JsonArray();
    params.add(book.getTitle());
    params.add(isbn);

    if(isbn != book.getIsbn()){
      result.fail(new IllegalArgumentException("invalid book isbn for update!"));
      return result;
    }

    sql.updateWithParams("update books set title = ? where isbn = ?",
      params, ar -> {
        if(ar.failed()){
          result.fail(ar.cause());
          return;
        }

        if(ar.result().getUpdated() != 1){
          result.fail(new IllegalStateException("no record updated" + ar.result()));
          return;
        }
        result.complete();
      });
    return result;
  }
}
