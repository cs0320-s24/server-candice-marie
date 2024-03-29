package edu.brown.cs.student.broadband;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import edu.brown.cs.student.main.broadband.ACSCensusDataSource;
import edu.brown.cs.student.main.broadband.exceptions.DataNotFoundException;
import edu.brown.cs.student.main.broadband.exceptions.DataSourceException;
import edu.brown.cs.student.main.broadband.exceptions.InputNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestACSCensusDataSource {
  private ACSCensusDataSource dataSource;
  private HttpURLConnection mockConnection;

  @BeforeEach
  public void setUp() {
    dataSource = new ACSCensusDataSource();
    mockConnection = mock(HttpURLConnection.class);
  }

  @Test
  public void testGetStateCode() throws IOException, DataSourceException {
    when(mockConnection.getResponseCode()).thenReturn(200);
    InputStream inputStream = new ByteArrayInputStream("[[\"Alabama\",\"01\"]]".getBytes());
    when(mockConnection.getInputStream()).thenReturn(inputStream);

    dataSource.getStateCode();

    // Verify statecode_map is populated correctly
    assertEquals("01", dataSource.getStatecode_map().get("Alabama"));
  }

  @Test
  public void testGetCountyCode() throws IOException, DataSourceException {
    when(mockConnection.getResponseCode()).thenReturn(200);
    InputStream inputStream =
        new ByteArrayInputStream("[[\"Autauga County, Alabama\",\"01\",\"001\"]]".getBytes());
    when(mockConnection.getInputStream()).thenReturn(inputStream);
    dataSource.getCountyCode("01");
    // Verify statecode_map is populated correctly
    assertEquals("001", dataSource.getCountycode_map().get("Autauga County"));
  }

  @Test
  public void testGetBroadBandPercentage() throws DataNotFoundException, DataSourceException {
    dataSource.getStateCode();
    dataSource.getACSVariables();
    assertNotNull(dataSource.getBroadbandPercentage("Elmore County", "Alabama"));
    assertThrows(
        DataNotFoundException.class,
        () -> dataSource.getBroadbandPercentage("Autauga County", "Alabama"));
    assertThrows(
        InputNotFoundException.class,
        () -> dataSource.getBroadbandPercentage("Autauga County", "xxx"));
    assertThrows(
        InputNotFoundException.class,
        () -> dataSource.getBroadbandPercentage("Autauga xxx", "Alabama"));

    assertThrows(
        DataNotFoundException.class,
        () -> dataSource.getBroadbandPercentage("Elmore County", "Alabama", "xxxx"));
  }
}
