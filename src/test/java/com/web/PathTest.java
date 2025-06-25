package com.web;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class PathTest {
  @Test
  void testPath() {
    String rootPath = Path.of("").toAbsolutePath().toString();
    System.out.println(rootPath);
    assertEquals(rootPath + "/data", Path.of("data").toAbsolutePath().toString());
    assertEquals(rootPath + "/data" + "/file1", Path.of("data", "file1").toAbsolutePath().toString());
  }
}
