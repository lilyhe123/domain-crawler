package com.web.api;

import com.web.data.HtmlPage;

import java.util.concurrent.CountDownLatch;

public interface ILinkExtractor extends IExecutor {
  void add(HtmlPage page, CountDownLatch latch);
  void setFrontier(IURLFrontier frontier);
}
