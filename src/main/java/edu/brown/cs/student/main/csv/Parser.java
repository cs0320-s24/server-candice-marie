package edu.brown.cs.student.main.csv;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Parses a CSV file using a provided CreatorFromRow to generate objects of type T for each row.
 *
 * <p>The Parser class is responsible for reading a CSV file from a provided Reader, parsing its
 * content, and creating objects of type T using the specified CreatorFromRow.
 *
 * @param <T> The type of objects to be created from each row of the CSV file.
 */
public class Parser<T> {

  private final Reader reader;
  private final CreatorFromRow<T> creater;
  private final boolean hasHeader;

  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
  /**
   * Constructs a Parser with the specified Reader, CreatorFromRow, and a flag indicating whether
   * the CSV file has a header.
   *
   * @param reader The Reader object for reading the CSV file.
   * @param creater The CreatorFromRow for creating objects of type T.
   * @param hasHeader A boolean flag indicating whether the CSV file has a header.
   */
  public Parser(Reader reader, CreatorFromRow<T> creater, boolean hasHeader) {
    this.reader = reader;
    this.creater = creater;
    this.hasHeader = hasHeader;
  }
  /**
   * Parses the CSV file and returns a ParsedResult containing the header map and parsed data.
   *
   * <p>If the CSV file has a header, it reads the header line, creates a header map, and uses it to
   * identify the columns. It then reads and processes the data, creating objects of type T using
   * the specified CreatorFromRow.
   *
   * @return A ParsedResult containing the header map and parsed data.
   * @throws Exception If there is an error during the parsing process.
   */
  public ParsedResult<T> parse() throws Exception {

    try (BufferedReader bufferedReader = new BufferedReader(this.reader)) {
      List<T> completeCSV = new ArrayList<T>();
      Map<String, Integer> headerMap = new HashMap<>();
      if (hasHeader) {
        String headerLine = bufferedReader.readLine();
        String[] header = regexSplitCSVRow.split(headerLine);
        int colID = 0;
        for (String colName : header) {
          headerMap.put(colName, colID);
          colID++;
        }
      }

      // Read and process the data
      String dataLine;
      while ((dataLine = bufferedReader.readLine()) != null) {
        List<String> row = List.of(regexSplitCSVRow.split(dataLine));
        completeCSV.add(creater.create(row));
      }

      return new ParsedResult<>(headerMap, completeCSV);
    }
  }
}
