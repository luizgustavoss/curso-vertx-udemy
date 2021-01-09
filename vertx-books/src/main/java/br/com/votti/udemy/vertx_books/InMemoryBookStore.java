package br.com.votti.udemy.vertx_books;


import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryBookStore {

  private Map<Long, Book> books = new HashMap<>();

  public InMemoryBookStore(){
    books.put(1L, new Book(1L, "Vert.x in Action"));
    books.put(2L, new Book(2L, "Building Microservices"));
  }

  public JsonArray getAll(){

    var all = new JsonArray();
    books.values().forEach(book -> {
      all.add(JsonObject.mapFrom(book));
    });
    return all;
  }

  public void add(final Book book) {
    books.put(book.getIsbn(), book);
  }

  public void delete(Long isbn) {
    books.remove(isbn);
  }

  public Book update(Long isbn, Book book) {

    if(isbn != book.getIsbn()){
      throw new IllegalArgumentException("Invalid ISBN number for book");
    }

    books.remove(isbn);
    books.put(isbn, book);
    return book;
  }

  public Optional<Book> getForId(Long isbn) {
    return Optional.ofNullable(books.get(isbn));
  }
}
