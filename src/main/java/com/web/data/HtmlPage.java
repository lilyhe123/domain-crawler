package com.web.data;

import java.util.concurrent.CountDownLatch;

import org.jsoup.nodes.Document;

public class HtmlPage {
  private final String url;
  private final CountDownLatch latch;
  private Document doc;

  public String getUrl() {
    return url;
  }

  public Document getDoc() {
    return doc;
  }
  public void setDoc(Document d) {
    doc = d;
  }
  public HtmlPage(String u, CountDownLatch l) {
    url = u;
    latch = l;
  }
  public void countDown() {
    latch.countDown();
  }
}
