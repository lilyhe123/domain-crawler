package com.web.api;

import com.web.data.HtmlPage;

public interface IHtmlDownloader extends IExecutor {
  void add(HtmlPage page);
}
