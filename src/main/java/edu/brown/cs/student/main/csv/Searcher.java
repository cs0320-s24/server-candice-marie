package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.csv.exceptions.InValidColumnIndexException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Searches for a specified element within a CSV dataset and returns matching rows.
 *
 * <p>The Searcher class is designed to search for a specified element within a dataset and retrieve
 * matching rows. It supports searching based on exact row element match for a given column index or
 * column name.
 *
 * @param <T> The type of objects Of each row element in the CSV dataset.
 */
public class Searcher<T> { // T is the single object element within a row
  private ParsedResult<List<T>> parsedResult;
  private List<List<T>> data;
  /**
   * Constructs a Searcher with the provided ParsedResult.
   *
   * @param parsedResult The ParsedResult containing the header map and parsed data.
   */
  public Searcher(ParsedResult<List<T>> parsedResult) {
    this.data = parsedResult.getData();
    this.parsedResult = parsedResult;
  }

  /**
   * Searches for the specified element in all columns of the dataset and returns matching rows.
   *
   * @param searchKey The element to search for.
   * @return A Map where the key is the row index and the value is the matching row.
   * @throws InValidColumnIndexException If an invalid column index is provided.
   */
  public Map<Integer, List<T>> search(T searchKey) throws InValidColumnIndexException {
    Map<Integer, List<T>> searchResults = new HashMap<>();
    if (data.isEmpty()) return searchResults;
    for (int colIndex = 0; colIndex < data.get(0).size(); colIndex++) {
      Map<Integer, List<T>> result = search(searchKey, colIndex);
      combineSearchResults(searchResults, result);
    }
    return searchResults;
  }

  /**
   * Searches for the specified element in a specific column of the dataset and returns matching
   * rows.
   *
   * @param searchKey The element to search for.
   * @param colIndex The index of the column to search within.
   * @return A Map where the key is the row index and the value is the matching row.
   * @throws InValidColumnIndexException If an invalid column index is provided.
   */
  public Map<Integer, List<T>> search(T searchKey, int colIndex)
      throws InValidColumnIndexException {
    Map<Integer, List<T>> searchResults = new HashMap<>();
    if (data.isEmpty()) return searchResults; // csv is empty
    if (colIndex < 0 | colIndex >= data.get(0).size()) {
      throw new InValidColumnIndexException(colIndex, data.get(0).size() - 1);
    }

    for (int rowId = 0; rowId < data.size(); rowId++) {
      List<T> row = data.get(rowId);
      if (row.get(colIndex).equals(searchKey)) {
        searchResults.put(rowId, row);
      }
    }
    return searchResults;
  }
  /**
   * Searches for the specified element in a column with the given name and returns matching rows.
   *
   * @param searchKey The element to search for.
   * @param colName The name of the column to search within.
   * @return A Map where the key is the row index and the value is the matching row.
   * @throws Exception If an error occurs during the search process.
   */
  public Map<Integer, List<T>> search(T searchKey, String colName) throws Exception {
    if (colName.equals("*")) {
      return search(searchKey);
    }
    if (isInteger(colName)) {
      return search(searchKey, Integer.parseInt(colName));
    }
    int colID = parsedResult.getColIDFromColName(colName);
    return search(searchKey, colID);
  }

  /**
   * Combine two result hashmap such that there is no duplicate in selected rows. The combination
   * changes result1 in place.
   *
   * @param result1 The first result hashmap.
   * @param result2 The second result hashmap.
   */
  public void combineSearchResults(Map<Integer, List<T>> result1, Map<Integer, List<T>> result2) {
    for (Map.Entry<Integer, List<T>> entry : result2.entrySet()) {
      Integer rowID = entry.getKey();
      List<T> row = entry.getValue();
      result1.put(rowID, row);
    }
  }
  /**
   * Checks if the given string is an integer.
   *
   * @param str The string to check.
   * @return True if the string is an integer, false otherwise.
   */
  private boolean isInteger(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
