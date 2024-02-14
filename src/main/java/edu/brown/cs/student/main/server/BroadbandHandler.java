 package edu.brown.cs.student.main.server;

 import com.squareup.moshi.JsonAdapter;
 import com.squareup.moshi.Moshi;
 import com.squareup.moshi.Types;
 import edu.brown.cs.student.main.broadband.ACSCensusDataSource;
 import java.lang.reflect.Type;
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
    String countyname = request.queryParams("County");
    String statename = request.queryParams("State");
    System.out.println("state=" + parameters);
    Moshi moshi = new Moshi.Builder().build();
    return 0;
    //.getBroadband(countyname, statename);
  }
 }
