package com.web.api;

import java.io.IOException;
import java.util.List;

public interface IURLFrontier extends IExecutor {
  boolean add(String url, boolean fromFile);
  void add(List<String> urlList);
  void start() throws IOException ;
  boolean isCompleted();
  boolean hasError();
}
