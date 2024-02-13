package edu.brown.cs.student.main.csv.exceptions;

public class CsvNotLoadedException extends Exception {
  public CsvNotLoadedException(String message) {
    super(message + "No csv is loaded.");
  }
}
