package com.web.api;

import com.web.data.HtmlPage;

public interface IHtmlConsumer extends IExecutor {
  public void add(HtmlPage page);
}
