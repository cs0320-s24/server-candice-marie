package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.csv.AccessCSV;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSVHandler implements Route {

  private final AccessCSV accessCSV;
  private final JsonAdapter<Map<String, Object>> adapter;

  public SearchCSVHandler(AccessCSV accessCSV) {
    this.accessCSV = accessCSV;
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(type);
  }

  @Override
  public String handle(Request request, Response response) {
    // TODO: call accessCSV.searchCSV()
    String query = request.queryParams("query");
    Map<String, Object> responseMap = new HashMap<>();
    if (query == null) {
      responseMap.put("result", "Exception");
      responseMap.put("error", "query param is not provided.");
      String responseMapString = adapter.toJson(responseMap);
      return responseMapString;
    }
    try {
      List<List<String>> searchedResult = this.accessCSV.searchCSV(query);
      responseMap.put("result", "success");
      responseMap.put("searchedResult", searchedResult);
    } catch (Exception e) {
      responseMap.put("result", "Exception");
      responseMap.put("error", e.toString());
      e.printStackTrace();
    }
    String responseMapString = adapter.toJson(responseMap);
    return responseMapString;
  }
}
