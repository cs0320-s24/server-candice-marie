package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.csv.AccessCSV;
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

    return 0;
  }
}
