package edu.brown.cs.student.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.brown.cs.student.main.csv.CreatorFromRow;
import edu.brown.cs.student.main.csv.FactoryFailureException;
import edu.brown.cs.student.main.csv.ListStringCreaterFromRow;
import edu.brown.cs.student.main.csv.ParsedResult;
import edu.brown.cs.student.main.csv.Parser;
import java.io.FileReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class TestParser {

  // Example class used for testing
  static class MyObject {
    private int id;
    private String name;

    public MyObject(int id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  // Simple implementation of CreatorFromRow for testing
  static class MyObjectCreator implements CreatorFromRow<MyObject> {
    @Override
    public MyObject create(List<String> row) throws FactoryFailureException {
      if (row.size() != 2)
        throw new FactoryFailureException(
            String.format(
                "Row size does not fit. Require size is 2; current row size is %d", row.size()),
            row);
      try {
        int id = Integer.parseInt(row.get(0));
        return new MyObject(id, row.get(1));
      } catch (NumberFormatException e) {
        throw new FactoryFailureException("MyObjectCreator fails to create ", row);
      }
    }
  }

  @Test
  void testParseListStringCreaterFromRow() throws Exception {
    String csvContent = "id,name\n1,John\n";

    Parser<List<String>> parser =
        new Parser<>(new StringReader(csvContent), new ListStringCreaterFromRow(), true);

    ParsedResult<List<String>> result = parser.parse();

    assertEquals(List.of(List.of("1", "John")), result.getData());
    assertEquals(new HashMap<>(Map.of("id", 0, "name", 1)), result.getHeaderMap());
    assertEquals(1, result.getData().size());
  }

  @Test
  void testParseFileReader() throws Exception {
    FileReader fileReader = new FileReader("data/census/dol_ri_earnings_disparity.csv");
    Parser<List<String>> parser = new Parser<>(fileReader, new ListStringCreaterFromRow(), true);

    ParsedResult<List<String>> result = parser.parse();

    assertEquals(
        new HashMap<>(
            Map.of(
                "State",
                0,
                "Data Type",
                1,
                "Average Weekly Earnings",
                2,
                "Number of Workers",
                3,
                "Earnings Disparity",
                4,
                "Employed Percent",
                5)),
        result.getHeaderMap());
    assertEquals(6, result.getData().size());
  }

  @Test
  void testParseMyObjectCreaterFromRow() throws Exception {
    String csvContent = "id,name\n1,John\n";
    Parser<MyObject> myObjectParser =
        new Parser<>(new StringReader(csvContent), new MyObjectCreator(), true);
    ParsedResult<MyObject> myObjectResult = myObjectParser.parse();

    assertEquals(1, myObjectResult.getData().get(0).id);
    assertEquals("John", myObjectResult.getData().get(0).name);

    String csvContentError = "id,name\nMarry,John\n";
    myObjectParser = new Parser<>(new StringReader(csvContentError), new MyObjectCreator(), true);
    assertThrows(FactoryFailureException.class, myObjectParser::parse);
  }

  @Test
  void testGetColIDFromColName() throws Exception {
    String csvHeader = "id,name";

    Parser<MyObject> parser = new Parser<>(new StringReader(csvHeader), null, true);

    ParsedResult<MyObject> result = parser.parse();

    assertEquals(1, result.getColIDFromColName("name"));
  }
}
