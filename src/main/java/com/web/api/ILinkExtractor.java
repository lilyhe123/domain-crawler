package com.web.api;

import com.web.data.HtmlPage;

public interface ILinkExtractor extends IExecutor {
  void add(HtmlPage page);
  void setFrontier(IURLFrontier frontier);
}
