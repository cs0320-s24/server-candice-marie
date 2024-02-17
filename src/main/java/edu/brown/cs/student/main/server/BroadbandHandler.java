package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.broadband.CensusDataSource;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import spark.Request;
import spark.Response;
import spark.Route;

/** This class implements a Spark Route for handling data requests from the broadband endpoint. */
public class BroadbandHandler implements Route {

  private final CensusDataSource state;
  private final JsonAdapter<Map<String, Object>> adapter;

  /**
   * Constructs a new BroadbandHandler with the specified CensusDataSource type and initializes a
   * JSON adapter for converting search results and other response data into JSON format.
   *
   * @param state The CensusDataSource to use for retrieving broadband data.
   */
  public BroadbandHandler(CensusDataSource state) {
    this.state = state;
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(type);
  }

  /**
   * Handles the HTTP request to retrieve broadband data. The request is expected to contain query
   * parameters for the county name, statename, and ACS variable(s) (optional) Responds with a JSON
   * object indicating the result and the output.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return The JSON string representation of the response.
   */
  @Override
  public Object handle(Request request, Response response) {
    Set<String> parameters = request.queryParams();
    Map<String, Object> responsemap = new HashMap<>();
    String countyname = request.queryParams("County");
    String statename = request.queryParams("State");
    String variablename = request.queryParams("ACSVariable");

    /* Checks that the input variables are not null*/
    if (countyname == null & statename == null) {
      responsemap.put("result", "Exception");
      responsemap.put("error", "county name and state name not provided");
      String responseMapString = adapter.toJson(responsemap);
      return responseMapString;
    }
    if (countyname == null) {
      responsemap.put("result", "Exception");
      responsemap.put("error", "county name not provided");
      String responseMapString = adapter.toJson(responsemap);
      return responseMapString;
    }
    if (statename == null) {
      responsemap.put("result", "Exception");
      responsemap.put("error", "state name not provided");
      String responseMapString = adapter.toJson(responsemap);
      return responseMapString;
    }
    /* If variable name is null, assume that user wants to retrieve broadband percentage */
    if (variablename == null) {
      try {
        String broadbandpercentage = state.getBroadbandPercentage(countyname, statename);
        responsemap.put("result", "success");
        responsemap.put("broadbandpercentage", broadbandpercentage);
        responsemap.put("county name", countyname);
        responsemap.put("state name", statename);
        String localdatetime = LocalDateTime.now().toString();
        responsemap.put("date and time", localdatetime);
      } catch (Exception e) {
        responsemap.put("result", "Exception");
        responsemap.put("error", e.toString());
        e.printStackTrace();
      }
      String responseMapString = adapter.toJson(responsemap);
      return responseMapString;
    }

    /* If there are multiple ACSVariable inputs, create a list of the variables */
    String[] acsvariable_list = variablename.split(",");
    if (!variablename.equals("S2802_C03_022E")) {
      if (acsvariable_list.length == 1) {
        try {
          String broadbandpercentage =
              state.getBroadbandPercentage(countyname, statename, variablename);
          responsemap.put("result", "success");
          responsemap.put(variablename, broadbandpercentage);
          responsemap.put("county name", countyname);
          responsemap.put("state name", statename);
          String localdatetime = LocalDateTime.now().toString();
          responsemap.put("date and time", localdatetime);
        } catch (Exception e) {
          responsemap.put("result", "Exception");
          responsemap.put("error", e.toString());
          e.printStackTrace();
        }
        String responseMapString = adapter.toJson(responsemap);
        return responseMapString;
      } else {
        /* Enter loop if there are multiple inputs for variable name */
        for (String s : acsvariable_list) {
          try {
            String broadbandpercentage = state.getBroadbandPercentage(countyname, statename, s);
            responsemap.put("result", "success");
            String localdatetime = LocalDateTime.now().toString();
            responsemap.put("date and time", localdatetime);
            responsemap.put("county name", countyname);
            responsemap.put("state name", statename);
            responsemap.put(s, broadbandpercentage);
          } catch (Exception e) {
            responsemap.put("result", "Exception");
            responsemap.put("error", e.toString());
            e.printStackTrace();
          }
        }
        String responseMapString = adapter.toJson(responsemap);
        return responseMapString;
      }
    } else {

      /* If variable is equal to broadband percentage, result should label output with
       * "broadband percentage:"
       * */

      try {
        String broadbandpercentage =
            state.getBroadbandPercentage(countyname, statename, variablename);
        responsemap.put("result", "success");
        responsemap.put("broadband percentage", broadbandpercentage);
        responsemap.put("county name", countyname);
        responsemap.put("state name", statename);
        String localdatetime = LocalDateTime.now().toString();
        responsemap.put("date and time", localdatetime);
      } catch (Exception e) {
        responsemap.put("result", "Exception");
        responsemap.put("error", e.toString());
        e.printStackTrace();
      }
      String responseMapString = adapter.toJson(responsemap);
      return responseMapString;
    }
  }
}
