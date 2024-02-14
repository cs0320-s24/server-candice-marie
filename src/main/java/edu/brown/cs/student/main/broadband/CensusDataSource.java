package edu.brown.cs.student.main.broadband;

import edu.brown.cs.student.main.broadband.exceptions.DataSourceException;
import edu.brown.cs.student.main.broadband.exceptions.InputNotFoundException;
import java.util.List;

public interface CensusDataSource {
  List<String> getBroadbandPercentage(String countyname, String statename) throws InputNotFoundException, DataSourceException;
}
