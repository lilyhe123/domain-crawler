package com.web.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jsoup.select.Elements;

import com.web.api.IHtmlConsumer;
import com.web.data.HtmlPage;

public class HtmlConsumer implements IHtmlConsumer {
  private final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(Consts.QUEUE_CAPACITY);
  private final ThreadPoolExecutor executor = new ThreadPoolExecutor(Consts.CONSUMER_POOL_SIZE, Consts.CONSUMER_POOL_SIZE,
          0, TimeUnit.SECONDS, queue);
 
  public void shutdown() {
    executor.shutdown();
  }
  public String status() {
    StringBuilder sb = new StringBuilder();
    sb.append("HtmlConsumer status: \n");
    sb.append(executor.toString());
    return sb.toString();
  }

  public void add(HtmlPage page){
    executor.submit(() -> consume(page));
  }

  private void consume(HtmlPage page) {
    try {
      String url = page.getUrl();
      Elements links = page.getDoc().select("a[href]");
      StringBuilder sb = new StringBuilder();
      sb.append("Visiting ").append(url).append("\n");
      sb.append("  link count: ").append(links.size()).append("\n");
      links.stream().limit(10).forEach
              (link -> sb.append("  - ").append(link.attr("href")).append("\n"));
      System.out.println(sb);
    } catch (Throwable t) {
      t.printStackTrace();
    } finally {
      page.countDown();
    }
  }
}
