package edu.brown.cs.student.main.broadband;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.broadband.exceptions.DataNotFoundException;
import edu.brown.cs.student.main.broadband.exceptions.DataSourceException;
import edu.brown.cs.student.main.broadband.exceptions.InputNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okio.Buffer;

/**
 * This class implements the CensusDataSource interface to retrieve data from the American Community
 * Survey (ACS).
 */
public class ACSCensusDataSource implements CensusDataSource {

  private Map<String, String> statecode_map;
  private Map<String, String> countycode_map;

  private Set<String> acsVariables;

  /**
   * Constructs a new ACSCensusDataSource with empty state and county code maps, and an ACS
   * variables set.
   */
  public ACSCensusDataSource() {
    statecode_map = new HashMap<>();
    countycode_map = new HashMap<>();
    acsVariables = new HashSet<>();
  }

  /**
   * Retrieves state codes from the Census API and stores them in a state code map.
   *
   * @throws DataSourceException if an error occurs during data retrieval.
   */
  public void getStateCode() throws DataSourceException {
    try {
      URL requestURL =
          new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      Type listStringObject = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listStringObject);
      /* Store Statename, Statecode as a list of list of strings (how it is represented in the API) */
      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (body == null) {
        throw new DataSourceException("malformed response from ACS");
      }
      for (List<String> i : body) {
        statecode_map.put(i.get(0), i.get(1));
      }
    } catch (IOException e) {
      throw new DataSourceException(e.getMessage());
    }
  }

  /**
   * Returns an unmodifiable view of the state code map.
   *
   * @return The unmodifiable state code map.
   */
  public Map<String, String> getStatecode_map() {
    return Collections.unmodifiableMap(statecode_map);
  }

  /**
   * Returns an unmodifiable view of the county code map.
   *
   * @return The unmodifiable county code map.
   */
  public Map<String, String> getCountycode_map() {
    return Collections.unmodifiableMap(countycode_map);
  }

  private static HttpURLConnection connect(URL requestURL) throws DataSourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection)) {
      throw new DataSourceException("unexpected: result of connection wasn't HTTP");
    }
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200) {
      throw new DataSourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    }
    return clientConnection;
  }

  /**
   * Retrieve the county codes for a given state from the Census API and stores them in a county
   * code map.
   *
   * @param statecode The state code for which county codes are to be retrieved.
   * @throws DataSourceException if an error occurs during data retrieval.
   */
  public void getCountyCode(String statecode) throws DataSourceException {
    countycode_map.clear();
    try {
      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + statecode);
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      Type listStringObject = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listStringObject);
      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (body == null) {
        throw new DataSourceException("malformed response from ACS");
      }
      for (List<String> i : body) {
        countycode_map.put(i.get(0).split(",")[0], i.get(2));
      }
    } catch (IOException e) {
      throw new DataSourceException(e.getMessage());
    }
  }

  /**
   * Retrieves ACS variables from the Census API and stores them in an ACS variables set.
   *
   * @throws DataSourceException If an error occurs during data retrieval.
   */
  public void getACSVariables() throws DataSourceException {
    try {
      URL requestURL = new URL("https", "api.census.gov", "/data/2021/acs/acs1/subject/variables");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      Type listStringObject = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listStringObject);
      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (body == null) {
        throw new DataSourceException("malformed response from ACS");
      }
      for (List<String> i : body) {
        acsVariables.add(i.get(0));
      }
    } catch (Exception e) {
      throw new DataSourceException(e.getMessage());
    }
  }

  /**
   * Retrieves the broadband percentage for a specific county and state, considering a specific ACS
   * variable.
   *
   * @param countyname The name of the county.
   * @param statename The name of the state.
   * @param acsVariable The ACS variable the caller wants to retrieve data for.
   * @return Broadband percentage as a String.
   * @throws InputNotFoundException if the county, state, or ACS variable is not found.
   * @throws DataSourceException if an error occurs during data retrieval.
   * @throws DataNotFoundException if the broadband data is not found for the given parameters.
   */
  public String getBroadbandPercentage(String countyname, String statename, String acsVariable)
      throws InputNotFoundException, DataSourceException, DataNotFoundException {
    if (!statecode_map.containsKey(statename)) {
      throw new InputNotFoundException("The state you entered (" + statename);
    }
    String state_code = statecode_map.get(statename);

    this.getCountyCode(state_code);
    if (!countycode_map.containsKey(countyname)) {
      throw new InputNotFoundException("The county you entered (" + countyname);
    }
    String county_code = countycode_map.get(countyname);

    try {
      /* Check which variable list to look at (subject/variables or profile/variables) */
      String param;
      if (acsVariable.equals("S2802_C03_022E")) {
        param = "subject";
      } else {
        param = "profile";
        if (!acsVariable.contains(acsVariable)) {
          throw new InputNotFoundException("The acs variable you entered (" + acsVariable);
        }
      }
      String endpointParam =
          "/data/2021/acs/acs1/%s/variables?get=NAME,%s&for=county:".formatted(param, acsVariable)
              + county_code
              + "&in=state:"
              + state_code;

      URL requestURL = new URL("https", "api.census.gov", endpointParam);
      System.out.println("URL=" + requestURL);
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      Type listStringObject = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listStringObject);
      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (body == null) {
        throw new DataSourceException("malformed response from ACS");
      }
      String broadband_percentage = body.get(1).get(1);

      if (broadband_percentage == null) return "null";
      return broadband_percentage;

    } catch (IOException e) {
      throw new DataSourceException(e.getMessage());
    } catch (DataSourceException e) {
      throw new DataNotFoundException(countyname, statename, acsVariable);
    }
  }

  /**
   * Retrieves the broadband percentage for a specific county and state, considering the default ACS
   * variable ("S2802_C03_022E" for broadband percentage).
   *
   * @param countyname The name of the county.
   * @param statename The name of the state.
   * @return The broadband percentage as a String.
   * @throws InputNotFoundException If the county or state is not found.
   * @throws DataSourceException If an error occurs during data retrieval.
   * @throws DataNotFoundException If the broadband data is not found for the given parameters.
   */
  public String getBroadbandPercentage(String countyname, String statename)
      throws InputNotFoundException, DataSourceException, DataNotFoundException {
    return getBroadbandPercentage(countyname, statename, "S2802_C03_022E");
  }
}
