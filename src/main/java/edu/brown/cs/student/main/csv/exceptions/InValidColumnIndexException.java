package edu.brown.cs.student.main.csv.exceptions;

public class InValidColumnIndexException extends Exception {

  public InValidColumnIndexException(String message) {
    super(message);
  }

  public InValidColumnIndexException(int colID, int maxColID) {
    super(
        String.format(
            "Column ID %d is invalid. Only accept column ID from 0 to %d", colID, maxColID));
  }
}
