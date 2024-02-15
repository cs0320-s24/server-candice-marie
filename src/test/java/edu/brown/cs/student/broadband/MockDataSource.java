package edu.brown.cs.student.broadband;

import static org.mockito.Mockito.*;

import edu.brown.cs.student.main.broadband.CensusDataSource;

public class MockDataSource implements CensusDataSource {
  private String broadbandp;

  public String getBroadbandPercentage(String countyname, String statename) throws Exception {
    broadbandp = "100";
    return broadbandp;
  }
}

