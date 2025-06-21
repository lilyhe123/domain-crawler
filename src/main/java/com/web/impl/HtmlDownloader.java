package com.web.impl;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.web.api.IHtmlConsumer;
import com.web.api.IHtmlDownloader;
import com.web.api.ILinkExtractor;
import com.web.data.HtmlPage;
import com.web.executor.TaskExecutor;
import com.web.executor.ThreadPoolTaskExecutor;
import com.web.executor.VirtualThreadTaskExecutor;

public class HtmlDownloader implements IHtmlDownloader {

  private final TaskExecutor executor;
      
  private final Connection session = Jsoup.newSession().timeout(Consts.HTTP_GET_TIMEOUT);
  private final ILinkExtractor linkExtractor;
  private final IHtmlConsumer htmlConsumer;

  // status
  private final AtomicLong totalDownloadedPages = new AtomicLong();
  private final AtomicLong failedPages = new AtomicLong();
  private final AtomicLong retryFailedPages = new AtomicLong();

  public HtmlDownloader(ILinkExtractor ex, IHtmlConsumer consumer, boolean useVirtualThread) {
    linkExtractor = ex;
    htmlConsumer = consumer;
    if (useVirtualThread) {
      executor = new VirtualThreadTaskExecutor(Consts.DOWLOAD_VT_PERMITS);
    } else {
      executor = new ThreadPoolTaskExecutor(Consts.DOWNLOAD_POOL_SIZE);
    }
  }
  @Override
  public void shutdown() {
    executor.shutdown();
  }
  @Override
  public String status() {
    return executor.toString();
  }

  @Override
  public void add(HtmlPage page, CountDownLatch latch) {
    executor.submit(this::crawl, page, latch);
  }
  // refer to https://jsoup.org/apidocs/org/jsoup/Connection.html#get() about different exceptions
  private void crawl(HtmlPage page, CountDownLatch latch) {
    String url = page.getUrl();
    boolean toRetry = true, success = false;
    int retryTimes = 0;

    while(!success && toRetry && retryTimes <= Consts.MAX_RETRIES) {
      if (retryTimes > 0) {
        long delay = Consts.BASE_RETRY_DELAY * (1 << retryTimes); // Exponential backoff
        System.out.println("Retry " + retryTimes + " times with delay " + delay);
        try {
          Thread.sleep(delay);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          return;
        }
      }
      retryTimes ++;
      try {
        Document doc = session.newRequest(url).get();
        page.setDoc(doc);
        linkExtractor.add(page, latch);
        htmlConsumer.add(page, latch);
        success = true;
      } catch (SocketTimeoutException ste) {
        System.err.println("Got SocketTimeoutException. " + ste.getMessage());
      } catch (HttpStatusException hse) {
        System.err.println("Got HttpStatusException. " + hse.getMessage());
        // Only retry when get 5xx code which means server error
        if (hse.getStatusCode() < 500 || hse.getStatusCode() > 599) {
          toRetry = false;
        }
      } catch (SocketException se) {
        System.err.println("Got SocketException. " + se.getMessage() + ". url: " + url);
      } catch (Throwable t) {
        toRetry = false;
        System.err.println("Exception when download page. url: " + url);
        t.printStackTrace();
      }
    }
    if (!success) {
      if (toRetry) {
        System.err.println("Failed to download a page after retry. url: " + url);
        retryFailedPages.incrementAndGet();
      } else {
        System.err.println("Failed to download a page. url: " + url);
        failedPages.incrementAndGet();
      }
      // since download the page failed, no page to extract and consume, 
      // count down latch to mark the page as completed.
      latch.countDown();
      latch.countDown();
    } else {
      totalDownloadedPages.incrementAndGet();
    }
  }
}
