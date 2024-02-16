package edu.brown.cs.student.broadband;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.broadband.ACSCensusDataSource;
import edu.brown.cs.student.main.broadband.CachedACSDataSource;
import edu.brown.cs.student.main.server.BroadbandHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestCachedDataSource {

  private final JsonAdapter<Map<String, Object>> adapter;
  private CachedACSDataSource cached_source;

  public TestCachedDataSource() {
    Moshi moshi = new Moshi.Builder().build();
    Type type = Types.newParameterizedType(Map.class, String.class, Object.class);
    adapter = moshi.adapter(type);
  }

  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  @BeforeEach
  public void setup() {
    ACSCensusDataSource source = new ACSCensusDataSource();
    try {
      source.getStateCode();
      source.getACSVariables();
    } catch (Exception e) {
      e.printStackTrace();
    }
    cached_source = new CachedACSDataSource(source);
    Spark.get("broadband", new BroadbandHandler(cached_source));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("broadband");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testCacheConsistency() throws Exception {
    // test that checks that cached and non-cached match

    // 3 variables
    HttpURLConnection clientConnection1 =
        tryRequest("broadband?County=Napa%20County&State=California&ACSVariable=S2802_C03_022E");
    assertEquals(200, clientConnection1.getResponseCode());
    Map<String, Object> response1 =
        adapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    assertEquals("success", response1.get("result"));
    String output1 =
        cached_source.getBroadbandPercentage("Napa County", "California", "S2802_C03_022E");
    assertEquals(response1.get("broadbandpercentage"), output1);

    // 2 variables
    HttpURLConnection clientConnection2 =
        tryRequest("broadband?County=Orange%20County&State=California");
    assertEquals(200, clientConnection2.getResponseCode());
    Map<String, Object> response2 =
        adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
    assertEquals("success", response2.get("result"));
    String output2 = cached_source.getBroadbandPercentage("Orange County", "California");
    assertEquals(response2.get("broadbandpercentage"), output2);
    //     System.out.println("OUTPUT=" +output1);
  }

  @Test
  public void testCacheHitMissStats() throws Exception {
    // checks that cache is keeping track of data already stored vs new data
    String output1 =
        cached_source.getBroadbandPercentage("Napa County", "California", "S2802_C03_022E");
    assertEquals(1, cached_source.getCacheMissCount());
    String output2 =
        cached_source.getBroadbandPercentage("Napa County", "California", "S2802_C03_022E");
    assertEquals(1, cached_source.getCacheMissCount());
    assertEquals(1, cached_source.getCacheHitCount());
    String output3 =
        cached_source.getBroadbandPercentage("Orange County", "California", "S2802_C03_022E");
    assertEquals(2, cached_source.getCacheMissCount());
    //     System.out.println("OUTPUT=" +output1);
  }

  @Test
  public void testCacheLoadStats() throws Exception {
    // checks that cache is keeping track of how many times it loads data
    String output1 =
        cached_source.getBroadbandPercentage("Napa County", "California", "S2802_C03_022E");
    assertEquals(1, cached_source.getCacheLoadCount());
    String output2 =
        cached_source.getBroadbandPercentage("Orange County", "California", "S2802_C03_022E");
    assertEquals(2, cached_source.getCacheLoadCount());
    String output3 =
        cached_source.getBroadbandPercentage(
            "San Francisco County", "California", "S2802_C03_022E");
    assertEquals(3, cached_source.getCacheLoadCount());
  }

  @Test
  public void testCacheEvictionStats() throws Exception {
    // checks that cache is evicting once inputs exceed maximum size (=3 as set in
    // CachedACSDataSource class)

    String output1 =
        cached_source.getBroadbandPercentage("Napa County", "California", "S2802_C03_022E");
    String output2 =
        cached_source.getBroadbandPercentage("Orange County", "California", "S2802_C03_022E");
    String output3 =
        cached_source.getBroadbandPercentage(
            "San Francisco County", "California", "S2802_C03_022E");
    String output4 =
        cached_source.getBroadbandPercentage("Los Angeles County", "California", "S2802_C03_022E");
    assertEquals(1, cached_source.getCacheEvictionCount());
    String output5 =
        cached_source.getBroadbandPercentage("Marin County", "California", "S2802_C03_022E");
    String output6 =
        cached_source.getBroadbandPercentage("Kern County", "California", "S2802_C03_022E");
    assertEquals(3, cached_source.getCacheEvictionCount());
  }
}
