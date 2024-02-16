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

/**
 * BroadbandHandler
 *
 * @p
 */
public class BroadbandHandler implements Route {

  private final CensusDataSource state;
  private final JsonAdapter<Map<String, Object>> adapter;

  public BroadbandHandler(CensusDataSource state) {
    this.state = state;
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(type);
  }

  @Override
  public Object handle(Request request, Response response) {
    Set<String> parameters = request.queryParams();
    System.out.println("params=" + parameters);
    Map<String, Object> responsemap = new HashMap<>();
    String countyname = request.queryParams("County");
    String statename = request.queryParams("State");
    String variablename = request.queryParams("ACSVariable");
    System.out.println("county=" + countyname);
    System.out.println("state=" + statename);
    System.out.println("vars=" + variablename);

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
    String[] acsvariable_list = variablename.split(",");
    System.out.println(acsvariable_list);
    if (!variablename.equals("S2802_C03_022E")) {
      System.out.println("varibale not equal to broadband");
      if (acsvariable_list.length == 1) {
        System.out.println("only one var");
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
        System.out.println("multiple vars");
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
      try {
        String broadbandpercentage =
            state.getBroadbandPercentage(countyname, statename, variablename);
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
  }
}
