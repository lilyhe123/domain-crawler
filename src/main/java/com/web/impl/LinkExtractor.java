package com.web.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.web.Util;
import com.web.api.ILinkExtractor;
import com.web.api.IURLFrontier;
import com.web.data.HtmlPage;

public class LinkExtractor implements ILinkExtractor  {
  private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(Consts.QUEUE_CAPACITY);
  private final ThreadPoolExecutor executor = new ThreadPoolExecutor(Consts.PARSER_POOL_SIZE, Consts.PARSER_POOL_SIZE,
          0, TimeUnit.SECONDS, queue);
  private IURLFrontier frontier;// TODO: do we need to set volatile

  @Override
  public void shutdown() {
    executor.shutdown();
  }
  @Override
  public String status() {
    StringBuilder sb = new StringBuilder();
    sb.append("LinkExtractor status: \n");
    sb.append(executor.toString());
    return sb.toString();
  }
  @Override
  public void add(HtmlPage page) {
    executor.execute(() -> {
      extract(page);
    });
  }
  @Override
  public void setFrontier(IURLFrontier f) {
    frontier = f;
  }
  private void extract(HtmlPage page) {
    try {
      String baseUrl = page.getUrl();
      Document doc = page.getDoc();
      Elements links = doc.select("a[href]");
      List<String> localUrls = new ArrayList<>();
      for (Element link : links) {
        String href = link.attr("href").trim();
        // ignore links referring section of the same page
        if (href.startsWith("#")) continue;
        String absoluteUrl = Util.normalizeUrl(baseUrl, href);
        localUrls.add(absoluteUrl);
        //log("normalizeUrl: " + baseUrl + ", " + href + "->" + absoluteUrl);
      }
      frontier.add(localUrls);

    } catch (Throwable t) {
      t.printStackTrace();
    } finally {
      page.countDown();
    }
  }
}
