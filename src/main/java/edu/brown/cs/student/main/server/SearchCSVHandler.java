package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.csv.AccessCSV;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSVHandler implements Route {

  private final AccessCSV accessCSV;

  public SearchCSVHandler(AccessCSV accessCSV) {
    this.accessCSV = accessCSV;
  }

  @Override
  public Object handle(Request request, Response response) {
    // TODO: call accessCSV.searchCSV()
    String query = request.queryParams("query");
    Map<String, Object> responseMap = new HashMap<>();
    if (query == null) {
      responseMap.put("result", "Exception");
      responseMap.put("error", "query param is not provided.");
      return responseMap;
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
    return responseMap;
  }
}
