package edu.brown.cs.student.main.broadband;

public interface CensusDataSource {
  String getBroadbandPercentage(String countyname, String statename) throws Exception;
}
