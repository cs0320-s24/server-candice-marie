package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.csv.AccessCSV;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;


public class LoadCSVHandler implements Route {

  private final AccessCSV accessCSV;
  private final JsonAdapter<Map<String,Object>> adapter;

  public LoadCSVHandler(AccessCSV accessCSV) {
    this.accessCSV = accessCSV;
    // Create a JSON adapter for the Map type
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(type);

  }

  @Override
  public String handle(Request request, Response response) {
    String path = request.queryParams("path");
    Map<String, Object> responseMap = new HashMap<>();
    String hasHeaderString = request.queryParams("hasHeader");
    if (path == null) {
      responseMap.put("result", "Exception");
      responseMap.put("error", "csv file path (path param) is not provided.");
    }
    if (!hasHeaderString.equals("true") && !hasHeaderString.equals("false")) {
      responseMap.put("result", "Exception");
      responseMap.put(
          "error",
          "Invalid hasHeader param value: %s.hasHeader param should be true or false. "
              .formatted(hasHeaderString));
    }
    boolean hasHeader = hasHeaderString.equals("true");
    try {

      accessCSV.LoadCSV(path, hasHeader);
      responseMap.put("result", "success");
      responseMap.put("message", "CSV file %s has been loaded".formatted(path));

    } catch (Exception e) {
      responseMap.put("result", "Exception");
      responseMap.put("error", e.toString());
      e.printStackTrace();
    }

    String responseMapString = adapter.toJson(responseMap);
    return responseMapString;
  }
}
