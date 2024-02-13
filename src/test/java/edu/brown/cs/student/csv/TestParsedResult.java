package edu.brown.cs.student.csv;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.csv.ListStringCreaterFromRow;
import edu.brown.cs.student.main.csv.ParsedResult;
import edu.brown.cs.student.main.csv.Parser;
import edu.brown.cs.student.main.csv.exceptions.HeaderFailureException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class TestParsedResult {

  @Test
  void testGetColIDFromColName() throws HeaderFailureException {
    Map<String, Integer> headerMap = new HashMap<>();
    headerMap.put("name", 0);
    headerMap.put("age", 1);

    ParsedResult<String> parsedResult = new ParsedResult<>(headerMap, List.of("John", "25"));

    assertEquals(0, parsedResult.getColIDFromColName("name"));
    assertThrows(
        HeaderFailureException.class, () -> parsedResult.getColIDFromColName("invalidColumnName"));

    assertThrows(
        HeaderFailureException.class,
        () ->
            new ParsedResult<>(new HashMap<>(), List.of("John", "25")).getColIDFromColName("name"));
  }

  @Test
  void testGetData() {
    List<String> dataRow = List.of("John", "25");
    ParsedResult<List<String>> parsedResult = new ParsedResult<>(new HashMap<>(), List.of(dataRow));

    assertEquals(1, parsedResult.getData().size());
    assertEquals(dataRow, parsedResult.getData().get(0));
  }

  @Test
  void testGetHeaderMap() {
    Map<String, Integer> headerMap = new HashMap<>();
    headerMap.put("name", 0);
    headerMap.put("age", 1);

    ParsedResult<String> parsedResult = new ParsedResult<>(headerMap, List.of("John", "25"));

    assertEquals(headerMap, parsedResult.getHeaderMap());
  }

  @Test
  void testParsedResultIterator() throws Exception {
    FileReader fileReader = new FileReader("data/stars/stardata.csv");
    Parser<List<String>> parser = new Parser<>(fileReader, new ListStringCreaterFromRow(), true);
    ParsedResult<List<String>> stars = parser.parse();
    //    for (List<String> star : stars) {
    //      System.out.println(star);
    //    }
  }
}
