package br.com.votti.vertx_starter;

public class Person {

  private Integer id;
  private String name;
  private Boolean lovesVertx;

  public Person(){
  }

  public Person(Integer id, String name, Boolean lovesVertx) {
    this();
    this.id = id;
    this.name = name;
    this.lovesVertx = lovesVertx;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Boolean getLovesVertx() {
    return lovesVertx;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setLovesVertx(Boolean lovesVertx) {
    this.lovesVertx = lovesVertx;
  }
}
