package edu.brown.cs.student.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.broadband.MockDataSource;
import edu.brown.cs.student.main.broadband.CensusDataSource;
import edu.brown.cs.student.main.csv.AccessCSV;
import edu.brown.cs.student.main.server.BroadbandHandler;
import edu.brown.cs.student.main.server.LoadCSVHandler;
import edu.brown.cs.student.main.server.SearchCSVHandler;
import edu.brown.cs.student.main.server.ViewCSVHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** This class tests BroadbandHandler, LoadCSVHandler, SearchCSVHandler, and ViewCSVHandler */
public class TestHandlers {
  private final JsonAdapter<Map<String, Object>> adapter;

  public TestHandlers() {
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    adapter = moshi.adapter(type);
  }

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  @BeforeEach
  public void setup() {
    /* Re-initialize state, etc. for _every_ test method run */
    AccessCSV accessCSV = new AccessCSV();
    /* In fact, restart the entire Spark server for every test! */
    Spark.get("loadcsv", new LoadCSVHandler(accessCSV));
    Spark.get("searchcsv", new SearchCSVHandler(accessCSV));
    Spark.get("viewcsv", new ViewCSVHandler(accessCSV));
    /* testing with mocked data source to ensure consistent functionality with various data sources */
    CensusDataSource mockedsource = new MockDataSource();
    //    ACSCensusDataSource source = new ACSCensusDataSource();
    //    try {
    //      source.getStateCode();
    //      source.getACSVariables();
    //    } catch (Exception e) {
    //      e.printStackTrace();
    //    }
    // CachedACSDataSource cached_source = new CachedACSDataSource(source);
    Spark.get("broadband", new BroadbandHandler(mockedsource));
    Spark.init();
    Spark.awaitInitialization(); /* don't continue until the server is listening */
  }

  @AfterEach
  public void teardown() {
    /* Gracefully stop Spark listening on both endpoints after each test */
    Spark.unmap("loadcsv");
    Spark.unmap("searchcsv");
    Spark.unmap("viewcsv");
    Spark.unmap("broadband");
    Spark.awaitStop(); /* don't proceed until the server is stopped */
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testLoadCSVSuccess() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?path=data/census/income_by_race.csv&hasHeader=true");
    assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", response.get("result"));
  }

  @Test
  public void testLoadCSVInvalidPath() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?path=data/xxx.csv&hasHeader=true");
    assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("Exception", response.get("result"));
  }

  @Test
  public void testLoadCSVInvalidHasHeader() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?path=data/census/income_by_race.csv&hasHeader=xx");
    assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("Exception", response.get("result"));
  }

  @Test
  public void testViewCSVHandlerSuccess() throws IOException {
    HttpURLConnection clientConnection1 =
        tryRequest("loadcsv?path=data/census/income_by_race.csv&hasHeader=true");
    assertEquals(200, clientConnection1.getResponseCode());
    HttpURLConnection clientConnection = tryRequest("viewcsv");
    assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", response.get("result"));
  }

  @Test
  public void testViewCSVHandlerFailure() throws IOException {
    HttpURLConnection clientConnection = tryRequest("viewcsv");
    assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("Exception", response.get("result"));
  }

  @Test
  public void testSearchCSVHandlerSuccess() throws IOException {
    HttpURLConnection clientConnection1 =
        tryRequest("loadcsv?path=data/census/income_by_race.csv&hasHeader=true");
    assertEquals(200, clientConnection1.getResponseCode());
    HttpURLConnection clientConnection = tryRequest("searchcsv?query=Race,White|Black");
    assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", response.get("result"));
  }

  @Test
  public void testSearchCSVHandlerFailure() throws IOException {
    HttpURLConnection clientConnection = tryRequest("searchcsv?query=Race,White|Black");
    assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("Exception", response.get("result"));
  }

  @Test
  public void testBroadbandHandlerSuccess() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("broadband?County=Kern+County&State=California");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnection2 =
        tryRequest("broadband?County=White%20County&State=Arkansas");
    assertEquals(200, clientConnection2.getResponseCode());
    Map<String, Object> response =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", response.get("result"));
  }

  @Test
  public void testBroadbandHandlerThreeParamsSuccess() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("broadband?County=Napa%20County&State=California&ACSVariable=S2704_C01_022E");
    assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", response.get("result"));
    assertEquals("100", response.get("S2704_C01_022E"));
  }

  @Test
  public void testBroadbandHandlerThreeParams() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("broadband?County=Napa%20County&State=California&ACSVariable=abcdefg");
    assertEquals(200, clientConnection.getResponseCode());
    Map<String, Object> response =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", response.get("result"));
    HttpURLConnection clientConnection1 =
        tryRequest("broadband?County=Napa%20County&State=California&ACSVariable=S2802_C03_022E");
    assertEquals(200, clientConnection1.getResponseCode());
    Map<String, Object> response1 =
        adapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    assertEquals("success", response1.get("result"));
    assertEquals("100", response1.get("broadbandpercentage"));
    HttpURLConnection clientConnection2 =
        tryRequest("broadband?County=Napa%20County&State=California&ACSVariable=S0102_C02_044E");
    assertEquals(200, clientConnection2.getResponseCode());
    Map<String, Object> response2 =
        adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
    assertEquals("success", response2.get("result"));
    assertEquals("100", response2.get("S0102_C02_044E"));
  }

  @Test
  public void testBroadbandHandlerFailure() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband");
    assertEquals(200, clientConnection.getResponseCode());
    // no county param
    HttpURLConnection clientConnection4 = tryRequest("broadband?State=California");
    assertEquals(200, clientConnection4.getResponseCode());
    // no state param
    HttpURLConnection clientConnection5 = tryRequest("broadband?County=Kern+County");
    assertEquals(200, clientConnection5.getResponseCode());
    Map<String, Object> response =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("Exception", response.get("result"));
  }
}
