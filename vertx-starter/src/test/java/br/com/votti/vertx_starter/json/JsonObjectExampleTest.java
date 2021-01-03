package br.com.votti.vertx_starter.json;

import br.com.votti.vertx_starter.Person;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JsonObjectExampleTest {

  @Test
  void jsonObjectCanBeMapped() {

    final JsonObject jsonObject = new JsonObject();
    jsonObject.put("id", 1);
    jsonObject.put("name", "Alice");
    jsonObject.put("loves_vertx", true);

    final String encoded =  jsonObject.encode();
    assertEquals("{\"id\":1,\"name\":\"Alice\",\"loves_vertx\":true}", encoded);

    final JsonObject decodedJsonObject = new JsonObject(encoded);
    assertEquals(jsonObject, decodedJsonObject);
  }

  @Test
  void jsonObjectCanBeCreatedFromMap() {

    final Map map = new HashMap<String, Object>();
    map.put("id", 1);
    map.put("name", "Alice");
    map.put("loves_vertx", true);

    final JsonObject jsonObject = new JsonObject(map);
    final String encoded =  jsonObject.encode();

    final JsonObject decodedJsonObject = new JsonObject(encoded);
    assertEquals(jsonObject, decodedJsonObject);

    assertEquals(map, jsonObject.getMap());
    assertEquals(1, jsonObject.getInteger("id"));
    assertEquals("Alice", jsonObject.getString("name"));
    assertEquals(true, jsonObject.getBoolean("loves_vertx"));
  }

  @Test
  void jsonArrayCanBeMapped(){
    final JsonArray jsonArray = new JsonArray();
    jsonArray.add(new JsonObject().put("id", 1));
    jsonArray.add(new JsonObject().put("name", "Alice"));
    jsonArray.add(new JsonObject().put("loves_vertx", true));

    assertEquals("[{\"id\":1},{\"name\":\"Alice\"},{\"loves_vertx\":true}]", jsonArray.encode());
  }

  @Test
  void canMapJavaObjects(){

    final Person person = new Person(1, "Alice", true);
    final JsonObject jsonObject = JsonObject.mapFrom(person);

    assertEquals(person.getId(), jsonObject.getInteger("id"));
    assertEquals(person.getName(), jsonObject.getString("name"));
    assertEquals(person.getLovesVertx(), jsonObject.getBoolean("lovesVertx"));

    final Person anotherPerson = jsonObject.mapTo(Person.class);
    assertNotNull(anotherPerson);
    assertEquals(1, anotherPerson.getId());
    assertEquals("Alice", anotherPerson.getName());
    assertEquals(true, anotherPerson.getLovesVertx());
  }
}
