package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.csv.AccessCSV;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSVHandler implements Route {

  private final AccessCSV accessCSV;

  public LoadCSVHandler(AccessCSV accessCSV) {
    this.accessCSV = accessCSV;
  }

  @Override
  public Object handle(Request request, Response response) {
    String path = request.queryParams("path");
    Map<String, Object> responseMap = new HashMap<>();
    String hasHeaderString = request.queryParams("hasHeader");
    if (path == null) {
      responseMap.put("result", "Exception");
      responseMap.put("error", "csv file path (path param) is not provided.");
      return responseMap;
    }
    if (!hasHeaderString.equals("true") && !hasHeaderString.equals("false")) {
      responseMap.put("result", "Exception");
      responseMap.put(
          "error",
          "Invalid hasHeader param value: %s.hasHeader param should be true or false. "
              .formatted(hasHeaderString));
      return responseMap;
    }
    boolean hasHeader = hasHeaderString.equals("true");
    try {

      accessCSV.LoadCSV(path, hasHeader);
      responseMap.put("result", "success");
      responseMap.put("message", "CSV file %s loaded".formatted(path));
      return responseMap;
    } catch (Exception e) {
      responseMap.put("result", "Exception");
      responseMap.put("error", e.toString());
      e.printStackTrace();
      return responseMap;
    }
  }
}
