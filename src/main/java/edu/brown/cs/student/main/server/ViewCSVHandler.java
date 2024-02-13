package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.csv.AccessCSV;
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
    return 0;
  }
}
