package edu.brown.cs.student.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.brown.cs.student.main.csv.ParsedResult;
import edu.brown.cs.student.main.csv.Searcher;
import edu.brown.cs.student.main.csv.exceptions.HeaderFailureException;
import edu.brown.cs.student.main.csv.exceptions.InValidColumnIndexException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestSearcher {

  private Searcher<String> searcher;
  private Searcher<String> searcherEmptyHeader;

  @BeforeEach
  void setUp() {
    Map<String, Integer> headerMap = Map.of("name", 0, "age", 1);
    List<List<String>> testData =
        List.of(List.of("John", "25"), List.of("Jane", "30"), List.of("Joe", "30"));
    ParsedResult<List<String>> parsedResult = new ParsedResult<>(headerMap, testData);
    searcher = new Searcher<>(parsedResult);
    ParsedResult<List<String>> parsedResultEmptyHeader =
        new ParsedResult<>(new HashMap<>(), testData);
    searcherEmptyHeader = new Searcher<>(parsedResultEmptyHeader);
  }

  @Test
  void testSearch() throws Exception {

    // search without column identifier
    Map<Integer, List<String>> result = searcher.search("John");
    assertEquals(1, result.size());
    assertEquals(List.of(List.of("John", "25")), new ArrayList<>((result.values())));

    // search by column index
    result = searcher.search("30", 1);
    assertEquals(2, result.size()); // Two rows have "30" in the age column
    assertEquals(
        List.of(List.of("Jane", "30"), List.of("Joe", "30")), new ArrayList<>(result.values()));

    // search by column name
    result = searcher.search("Jane", "name");
    assertEquals(1, result.size());
    assertEquals(List.of(List.of("Jane", "30")), new ArrayList<>(result.values()));

    // Empty header: search without column identifier
    result = searcherEmptyHeader.search("John");
    assertEquals(1, result.size());
    assertEquals(List.of(List.of("John", "25")), new ArrayList<>(result.values()));

    // Empty header: search by column index
    result = searcherEmptyHeader.search("30", 1);
    assertEquals(2, result.size()); // Two rows have "30" in the age column
    assertEquals(
        List.of(List.of("Jane", "30"), List.of("Joe", "30")), new ArrayList<>(result.values()));

    assertThrows(HeaderFailureException.class, () -> searcherEmptyHeader.search("John", "name"));

    // test exceptions
    assertThrows(InValidColumnIndexException.class, () -> searcher.search("John", 10));

    assertThrows(HeaderFailureException.class, () -> searcher.search("John", "invalidColumn"));
  }
}
