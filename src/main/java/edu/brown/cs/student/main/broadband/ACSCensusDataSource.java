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
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okio.Buffer;
import java.util.*;
import java.time.LocalDate;

public class ACSCensusDataSource implements CensusDataSource {

  private Map<String, String> statecode_map;
  private Map<String, String> countycode_map;

  public ACSCensusDataSource() {
    statecode_map = new HashMap<>();
    countycode_map = new HashMap<>();
  }

  public void getStateCode() throws DataSourceException {
    try {
      System.out.println("trying to get state code");
      URL requestURL =
          new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      Type listStringObject = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listStringObject);
      //Store Statename, Statecode as a list of list of strings (how it is represented in the API)
      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (body == null) {
        throw new DataSourceException("malformed response from ACS");
      }
      // body = List<List<String>> that stores the statename, statecode
      // change List<List<String>> to HashMap to store data
      for (List<String> i : body) {
        statecode_map.put(i.get(0), i.get(1));
      }
    } catch (IOException e) {
      throw new DataSourceException(e.getMessage());
    }
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

  public void getCountyCode(String statecode) throws DataSourceException {
    countycode_map.clear();
    try {
      System.out.println("trying to get county code");
      URL requestURL =
          //https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:*
          new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + statecode);
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      Type listStringObject = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listStringObject);
      //Store Statename, Statecode as a list of list of strings (how it is represented in the API)
      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (body == null) {
        throw new DataSourceException("malformed response from ACS");
      }
      // body = List<List<String>> that stores the statename, statecode
      // change List<List<String>> to HashMap to store data
      for (List<String> i : body) {
        countycode_map.put(i.get(0).split(",")[0], i.get(2));
      }
    } catch (IOException e) {
      throw new DataSourceException(e.getMessage());
    }
  }
  public String getBroadbandPercentage(String countyname, String statename) throws InputNotFoundException, DataSourceException, DataNotFoundException {
    System.out.println(statename);
    System.out.println(statecode_map);
    System.out.println(countyname);
    System.out.println("county code:" + countycode_map);
    //https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:06
    if (!statecode_map.containsKey(statename)) {
      throw new InputNotFoundException("The state you entered (" + statename);
    }
    String state_code = statecode_map.get(statename);

    this.getCountyCode(state_code);
    if (!countycode_map.containsKey(countyname)) {
      throw new InputNotFoundException("The county you entered (" + countyname);
    }
    String county_code = countycode_map.get(countyname);

    System.out.println(state_code);
    System.out.println(county_code);

    try {
      URL requestURL =
          new URL("https", "api.census.gov", "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:" + county_code +"&in=state:" + state_code);
      System.out.println("URL=" + requestURL);
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      Type listStringObject = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listStringObject);
      //Store Statename, Statecode as a list of list of strings (how it is represented in the API)
      List<List<String>> body =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (body == null) {
        throw new DataSourceException("malformed response from ACS");
      }
      String broadband_percentage = body.get(1).get(1);
      System.out.println(body);
      System.out.println(broadband_percentage);
      return broadband_percentage;

    } catch (IOException e) {
      throw new DataSourceException(e.getMessage());
    } catch (DataSourceException e) {
      throw new DataNotFoundException(countyname, statename);
    }
  }
}
