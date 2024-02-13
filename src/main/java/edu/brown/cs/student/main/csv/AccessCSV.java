package edu.brown.cs.student.main.csv;

import static edu.brown.cs.student.main.csv.Parser.regexSplitCSVRow;

import edu.brown.cs.student.main.csv.exceptions.CsvNotLoadedException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessCSV {
  private Parser<List<String>> parser;
  private ParsedResult<List<String>> parsedResult;
  private Searcher<String> searcher;

  public AccessCSV() {
    parser = null;
  }

  public void LoadCSV(String filePath, boolean hasHeader) throws Exception {
    if (!isUnderDataDirectory(filePath)) {
      throw new FileNotFoundException(
          "Illegal file path: %s. Provided file should be under data/./n".formatted(filePath));
    }
    FileReader fileReader = new FileReader(filePath);
    this.parser = new Parser<>(fileReader, new ListStringCreaterFromRow(), hasHeader);
    this.parsedResult = parser.parse();
    this.searcher = new Searcher<>(this.parsedResult);
  }

  public List<List<String>> ViewCSV() throws CsvNotLoadedException {
    if (parser == null) {
      throw new CsvNotLoadedException("Cannot view csv. ");
    }
    return parsedResult.getData();
  }
  /**
   * Performs a query search using the specified query string and searcher.
   *
   * @param queryString The query string.
   * @throws Exception If an error occurs during the query search.
   */
  public List<List<String>> searchCSV(String queryString) throws Exception {
    if (parser == null) {
      throw new CsvNotLoadedException("Cannot view csv. ");
    }
    String[] queries = queryString.split("&&");
    Map<Integer, List<String>> searchResults = new HashMap<>();
    List<String> containQueries = new ArrayList<>();
    List<String> excludeQueries = new ArrayList<>();

    // put queries in categoties
    for (String query : queries) {
      if (query.contains("^")) {
        excludeQueries.add(query);
      } else {
        containQueries.add(query);
      }
    }

    for (String query : containQueries) {
      String[] qElements = regexSplitCSVRow.split(query);
      String[] orConditions = qElements[1].split("\\|");
      for (String condition : orConditions) {
        Map<Integer, List<String>> result = searcher.search(condition, qElements[0]);
        searcher.combineSearchResults(searchResults, result);
      }
    }

    Map<Integer, List<String>> toBeExclude = new HashMap<>();

    for (String query : excludeQueries) {
      String removeNegate = query.replace("^", "");
      String[] qElements = regexSplitCSVRow.split(removeNegate);
      String[] andConditions = qElements[1].split("&");
      for (String condition : andConditions) {
        Map<Integer, List<String>> result = searcher.search(condition, qElements[0]);
        searcher.combineSearchResults(toBeExclude, result);
      }
    }

    if (!excludeQueries.isEmpty()) {
      for (Map.Entry<Integer, List<String>> rowId : searchResults.entrySet()) {
        if (toBeExclude.containsKey(rowId)) {
          searchResults.remove(rowId);
        }
      }
    }
    List<List<String>> searchResultList = new ArrayList<>();
    searchResultList.addAll(searchResults.values());
    return searchResultList;
  }

  private static boolean isUnderDataDirectory(String filePath) {
    Path normalizedPath = Paths.get(filePath).normalize();

    Path dataDirectory = Paths.get("data").normalize();

    return normalizedPath.startsWith(dataDirectory);
  }
}
