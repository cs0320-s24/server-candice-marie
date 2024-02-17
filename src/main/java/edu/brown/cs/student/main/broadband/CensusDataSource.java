package edu.brown.cs.student.main.broadband;

/**
 * The CensusDataSource interface provides methods to retrieve broadband percentage data from a
 * census data source.
 */
public interface CensusDataSource {

  /**
   * Retrieves the broadband percentage for a specific county and state.
   *
   * @param countyname The name of the county.
   * @param statename The name of the state.
   * @return The broadband percentage as a String.
   * @throws Exception If an error occurs during data retrieval.
   */
  String getBroadbandPercentage(String countyname, String statename) throws Exception;

  /**
   * Retrieves the broadband percentage for a specific county and state, considering a specific
   * variable from ACS's official variable list.
   *
   * @param countyname The name of the county.
   * @param statename The name of the state.
   * @param variable A specific variable to consider.
   * @return The broadband percentage as a String.
   * @throws Exception If an error occurs during data retrieval.
   */
  String getBroadbandPercentage(String countyname, String statename, String variable)
      throws Exception;
}
