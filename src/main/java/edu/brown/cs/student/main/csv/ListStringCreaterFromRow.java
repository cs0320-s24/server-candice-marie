package edu.brown.cs.student.main.csv;

import java.util.List;

/**
 * Implementation of the CreatorFromRow interface for creating a List<String> from a row of strings.
 */
public class ListStringCreaterFromRow implements CreatorFromRow<List<String>> {

  /**
   * Creates a List<String> from a row of strings.
   *
   * @param row a row of strings.
   * @return A List<String> containing the fields from the input row.
   * @throws FactoryFailureException If there is a failure in the creation process.
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}
