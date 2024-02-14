package edu.brown.cs.student.main.broadband.exceptions;

public class InputNotFoundException extends RuntimeException {
  public InputNotFoundException(String message) {
    super(message + ") is not found in dataset");
  }
}
