package iudx.apd.acl.server.notification;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Tuple;
import java.util.List;
import java.util.Map;

@DataObject(generateConverter = true)
public class TupleBuilder implements Tuple {

  private final Tuple tuple;
//  public TupleBuilder(Object... queryParams)
//  {
//    // param could be list
//    // varargs
//    // Tuple itself
//    tuple = Tuple.tuple();
//    for(Object param: queryParams)
//    {
//      tuple.addValue(param);
//    }
//
//    TupleBuilderConverter.fromJson(queryParams, this);
//
//  }


  public TupleBuilder(Tuple tuple) {
    this.tuple = tuple;
    TupleBuilderConverter.fromJson((Iterable<Map.Entry<String, Object>>) this.tuple, this);

  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    TupleBuilderConverter.toJson(this, jsonObject);
    return jsonObject;
  }


  public Tuple getTuple() {
    return this.tuple;
  }

  /**
   * Get an object value at {@code pos}.
   *
   * @param pos the position
   * @return the value
   */
  @Override
  public Object getValue(int pos) {
    return tuple.get(Object.class, pos);
  }

  /**
   * Add an object value at the end of the tuple.
   *
   * @param value the value
   * @return a reference to this, so the API can be used fluently
   */
  @Override
  public Tuple addValue(Object value) {
    return tuple;
  }

  /**
   * @return the tuple size
   */
  @Override
  public int size() {
    return tuple.size();
  }

  @Override
  public void clear() {
    tuple.clear();
  }

  /**
   * @return the list of types built from the tuple
   */
  @Override
  public List<Class<?>> types() {
    return List.of();
  }
}
