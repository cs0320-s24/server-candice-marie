package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.csv.exceptions.HeaderFailureException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Represents the result of parsing a CSV file, containing a header map and parsed data.
 *
 * <p>A ParsedResult object encapsulates the header map, which maps column names to corresponding
 * column indices, and the parsed data, represented as a list of generic type T.
 *
 * @param <T> Represents the row of the parsed data.
 */
public class ParsedResult<T> implements Iterable<T> {
  private Map<String, Integer> headerMap; // map column name to colID
  private List<T> data;

  /**
   * Constructs a ParsedResult object with the specified header map and parsed data.
   *
   * @param headerMap A Map<String, Integer> representing the header map.
   * @param data A List<T> representing the parsed data.
   */
  public ParsedResult(Map<String, Integer> headerMap, List<T> data) {
    this.headerMap = headerMap;
    this.data = data;
  }

  /**
   * Gets the column ID corresponding to the given column name.
   *
   * @param colName The column name for which to retrieve the column ID.
   * @return The column ID corresponding to the given column name.
   * @throws HeaderFailureException If the header does not exist or the column name is not found.
   */
  public int getColIDFromColName(String colName) throws HeaderFailureException {
    if (this.headerMap.isEmpty()) {
      throw new HeaderFailureException("Header does not exist. Please index using column index");
    }
    if (!this.headerMap.containsKey(colName)) {
      throw new HeaderFailureException(String.format("Column \"%s\" does not exist.", colName));
    }
    return headerMap.get(colName);
  }
  /**
   * Gets the parsed data.
   *
   * @return A List<T> representing the parsed data.
   */
  public List<T> getData() {
    return this.data;
  }
  /**
   * Gets the header map.
   *
   * @return A Map<String, Integer> representing the header map.
   */
  public Map<String, Integer> getHeaderMap() {
    return this.headerMap;
  }

  @Override
  public Iterator<T> iterator() {
    return new ParsedResultIterator();
  }

  private class ParsedResultIterator implements Iterator<T> {
    private T nextRow;
    private Iterator<T> iterator;

    public ParsedResultIterator() {
      iterator = data.iterator();
      nextRow = iterator.next();
    }

    @Override
    public boolean hasNext() {
      return nextRow != null;
    }

    @Override
    public T next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      nextRow = iterator.next();
      return nextRow;
    }
  }
}
