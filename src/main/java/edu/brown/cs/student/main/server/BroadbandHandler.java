 package edu.brown.cs.student.main.server;
 import java.time.LocalDate;
 import com.squareup.moshi.JsonAdapter;
 import com.squareup.moshi.Moshi;
 import com.squareup.moshi.Types;
 import edu.brown.cs.student.main.broadband.ACSCensusDataSource;
 import java.lang.reflect.Type;
 import java.time.LocalDateTime;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import spark.Request;
 import spark.Response;
 import spark.Route;

 public class BroadbandHandler implements Route {

  private final ACSCensusDataSource state;
  public BroadbandHandler(ACSCensusDataSource state) {
    this.state  = state;
  }
  @Override
  public Object handle(Request request, Response response) {
    Set<String> parameters = request.queryParams();
    System.out.println("params=" + parameters);
    Map<String, Object> responsemap = new HashMap<>();
    String countyname = request.queryParams("County");
    String statename = request.queryParams("State");
    if (countyname == null & statename ==null) {
      responsemap.put("result", "Exception");
      responsemap.put("error", "county name and state name not provided");
      return responsemap;
    }
    if (countyname == null) {
      responsemap.put("result", "Exception");
      responsemap.put("error", "county name not provided");
      return responsemap;
    }
    if (statename == null) {
      responsemap.put("result", "Exception");
      responsemap.put("error", "state name not provided");
      return responsemap;
    }
    System.out.println("state=" + parameters);

    try {
      String broadbandpercentage = state.getBroadbandPercentage(countyname, statename);
      responsemap.put("result", "success");
      responsemap.put("broadbandpercentage", broadbandpercentage);
      responsemap.put("county name", countyname);
      responsemap.put("state name", statename);
      String localdatetime = LocalDateTime.now().toString();
      responsemap.put("date and time", localdatetime);
    } catch (Exception e) {
      responsemap.put("result", "exception");
      responsemap.put("error", e.toString());
      e.printStackTrace();
    }
    return responsemap;
    //.getBroadband(countyname, statename);
  }
 }
