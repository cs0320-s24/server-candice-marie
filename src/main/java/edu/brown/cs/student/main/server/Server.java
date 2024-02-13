package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.csv.AccessCSV;
import spark.Spark;

public class Server {
  public static void main(String[] args) {
    int port = 3232;
    Spark.port(port);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    AccessCSV accessCSV = new AccessCSV();
    Spark.get("loadcsv", new LoadCSVHandler(accessCSV));
    Spark.get("searchcsv", new SearchCSVHandler(accessCSV));
    Spark.get("viewcsv", new ViewCSVHandler(accessCSV));
    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}
