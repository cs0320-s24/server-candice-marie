package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.csv.AccessCSV;
import edu.brown.cs.student.main.csv.exceptions.CsvNotLoadedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewCSVHandler implements Route {
  private final AccessCSV accessCSV;

  public ViewCSVHandler(AccessCSV accessCSV) {
    this.accessCSV = accessCSV;
  }

  @Override
  public Object handle(Request request, Response response) {
    // TODO: call accessCSV.viewCSV()
    Map<String, Object> responseMap = new HashMap<>();
    try {
      List<List<String>> csv = this.accessCSV.ViewCSV();
      responseMap.put("result", "success");
      responseMap.put("loadedCSV", csv);
    } catch (CsvNotLoadedException e) {
      responseMap.put("result", "Exception");
      responseMap.put("error", e.toString());
      e.printStackTrace();
    }
    return responseMap;
  }
}
