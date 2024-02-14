package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.broadband.ACSCensusDataSource;
import edu.brown.cs.student.main.broadband.exceptions.DataSourceException;
import edu.brown.cs.student.main.broadband.exceptions.InputNotFoundException;
import edu.brown.cs.student.main.csv.AccessCSV;
import javax.xml.crypto.Data;
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
    ACSCensusDataSource source = new ACSCensusDataSource();
    try {
      source.getStateCode();
      source.getCountyCode("06");
    } catch(DataSourceException e) {
      e.printStackTrace();
    }
    try {
      source.getBroadbandPercentage("Stone County", "Arkansas");
    } catch(DataSourceException | InputNotFoundException e) {
      e.printStackTrace();
    }

    Spark.get("broadband", new BroadbandHandler(new ACSCensusDataSource()));

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}
