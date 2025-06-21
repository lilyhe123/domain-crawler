package com.web.file;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
/**
 * To store urls that has already been seen but might not finish crawling yet.
 * One url a line.
 */
public class StringListFile {
  private final Path path;
  private final BufferedWriter writer;

  public StringListFile(Path p) throws IOException {
    path = p;
    writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, 
      StandardOpenOption.APPEND, StandardOpenOption.CREATE);
  }

  public List<String> readAll() throws IOException {
    List<String> list = new ArrayList<>();
    if (!Files.exists(path)) return list;
    try (Stream<String> stream = Files.lines(path)) {
      stream.forEach(list::add);
    }
    return list;
  }

  public synchronized void append(List<String> list) throws IOException {
    for (String str : list) {
      writer.write(str, 0, str.length());
      writer.newLine();
    }
    writer.flush();
  }
  
  public synchronized void close() {
    try {
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
