package edu.brown.cs.student.main.csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AccessCSV {
  Parser<List<String>> parser;

  public AccessCSV() {
    parser = null;
  }

  public void LoadCSV(String filePath, boolean hasHeader) throws FileNotFoundException {
    if (!isUnderDataDirectory(filePath)) {
      throw new FileNotFoundException(
          "Illegal file path: %s. Provided file should be under data/./n".formatted(filePath));
    }
    FileReader fileReader = new FileReader(filePath);
    parser = new Parser<>(fileReader, new ListStringCreaterFromRow(), hasHeader);
  }

  private static boolean isUnderDataDirectory(String filePath) {
    Path normalizedPath = Paths.get(filePath).normalize();

    Path dataDirectory = Paths.get("data").normalize();

    return normalizedPath.startsWith(dataDirectory);
  }
}
