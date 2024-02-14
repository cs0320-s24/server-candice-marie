package edu.brown.cs.student.main.broadband.exceptions;

public class DataNotFoundException extends Exception {
  public DataNotFoundException(String countyname, String statename, String acsVariable) {
    super(
        "Broadband percentage for county %s, state %s does not exist for acs variable %s"
            .formatted(countyname, statename, acsVariable));
  }
}
