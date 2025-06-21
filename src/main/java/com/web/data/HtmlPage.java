package com.web.data;

import org.jsoup.nodes.Document;

//TODO: prevent direct reference to the Document class
public class HtmlPage {
  public String getUrl() {
    return url;
  }
  private String url;

  public Document getDoc() {
    return doc;
  }
  public void setDoc(Document d) {
    doc = d;
  }
  private Document doc;

  public HtmlPage(String u) {
    url = u;
  }
  public void complete() {
    //TODO: update storage
  }
}
