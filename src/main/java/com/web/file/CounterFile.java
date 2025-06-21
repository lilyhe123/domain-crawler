package com.web.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
/**
 * Store the total number of crawled pages.
 */
public class CounterFile {
  private final Path path;

  public CounterFile(Path p) {
    path = p;
  }
  public void update(int val) {
    try {
      Files.writeString(path, Integer.toString(val), 
      StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
  public int get() throws IOException {
    if (!Files.exists(path)) return 0;
      return Integer.parseInt(Files.readString(path).trim());
  }
}
