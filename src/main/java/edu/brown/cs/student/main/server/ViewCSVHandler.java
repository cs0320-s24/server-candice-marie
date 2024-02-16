package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.csv.AccessCSV;
import edu.brown.cs.student.main.csv.exceptions.CsvNotLoadedException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * ViewCSVHandler is a Spark Route implementation that handles requests to view the contents
 * of a loaded CSV file. It utilizes an instance of {@link AccessCSV} to retrieve the CSV data
 * and returns a JSON response containing the CSV contents or an error message if no CSV
 * file is currently loaded.
 */
public class ViewCSVHandler implements Route {
  private final AccessCSV accessCSV;
  private final JsonAdapter<Map<String, Object>> adapter;
  /**
   * Constructs a ViewCSVHandler with a specified AccessCSV instance for handling CSV content viewing.
   * Initializes a JSON adapter for converting CSV content and other response data into JSON format.
   *
   * @param accessCSV The {@link AccessCSV} instance to use for accessing loaded CSV content.
   */
  public ViewCSVHandler(AccessCSV accessCSV) {
    this.accessCSV = accessCSV;
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(type);
  }
  /**
   * Handles a request to view the contents of the currently loaded CSV file. Responds with a JSON
   * object that includes either the contents of the CSV file or an error message if no CSV file
   * is loaded.
   *
   * @param request The Spark request object. This handler does not use request parameters as
   *                viewing the CSV does not require input from the request.
   * @param response The Spark response object, used to modify the response's properties.
   * @return A string representing a JSON object with the result of the operation, including the
   *         loaded CSV content or an error message.
   */
  @Override
  public String handle(Request request, Response response) {
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
    String responseMapString = adapter.toJson(responseMap);
    return responseMapString;
  }
}
