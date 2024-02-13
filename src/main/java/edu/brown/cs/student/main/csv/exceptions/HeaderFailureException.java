package edu.brown.cs.student.main.csv.exceptions;

public class HeaderFailureException extends Exception {
  public HeaderFailureException(String message) {
    super("Header Failure: " + message);
  }
}
