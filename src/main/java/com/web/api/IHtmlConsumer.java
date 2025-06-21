package com.web.api;

import com.web.data.HtmlPage;

import java.util.concurrent.CountDownLatch;

public interface IHtmlConsumer extends IExecutor {
  public void add(HtmlPage page, CountDownLatch latch);
}
