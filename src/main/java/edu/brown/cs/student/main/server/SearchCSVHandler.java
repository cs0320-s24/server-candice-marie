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

/**
 * SearchCSVHandler is a Spark Route implementation that handles requests to search within a CSV
 * file. It utilizes an instance of {@link AccessCSV} to perform the search operation based on the
 * query provided in the request and returns a JSON response with the search results.
 */
public class SearchCSVHandler implements Route {

  private final AccessCSV accessCSV;
  private final JsonAdapter<Map<String, Object>> adapter;

  /**
   * Constructs a SearchCSVHandler with a specified AccessCSV instance for handling CSV searches.
   * Initializes a JSON adapter for converting search results and other response data into JSON
   * format.
   *
   * @param accessCSV The {@link AccessCSV} instance to use for searching within CSV files.
   */
  public SearchCSVHandler(AccessCSV accessCSV) {
    this.accessCSV = accessCSV;
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(type);
  }
  /**
   * Handles a request to search within a loaded CSV file. The request should contain a query
   * parameter named 'query' specifying the search criteria. The method responds with a JSON object
   * indicating the result of the search operation, which can include the search results or an error
   * message.
   *
   * @param request The Spark request object, containing the 'query' parameter.
   * @param response The Spark response object, used to modify the response's properties.
   * @return A string representing a JSON object with the result of the search operation, including
   *     either the search results or an error message.
   */
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

      if (searchedResult.isEmpty()) {
        responseMap.put("result", "Exception");
        responseMap.put("searchedResult", "No entries found with query: %s.".formatted(query));
      } else {
        responseMap.put("result", "success");
        responseMap.put("searchedResult", searchedResult);
      }

    } catch (Exception e) {
      responseMap.put("result", "Exception");
      responseMap.put("error", e.toString());
      e.printStackTrace();
    }
    String responseMapString = adapter.toJson(responseMap);
    return responseMapString;
  }
}
