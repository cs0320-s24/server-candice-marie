package edu.brown.cs.student.main.broadband.exceptions;

public class DataSourceException extends Exception {
  public DataSourceException(String message) {
    super(message + "data source exception");
  }
}
