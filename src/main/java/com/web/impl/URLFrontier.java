package com.web.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.web.Util;
import com.web.api.IHtmlDownloader;
import com.web.api.IURLFrontier;
import com.web.data.HtmlPage;
import com.web.file.CounterFile;
import com.web.file.StringListFile;

/**
 * Maintain in memory:
 * - urlSet: all urls that have been seen. To prevent crawl a same page multiple times.
 * - urlQueue: urls that has been scheduled but not completed yet.
 * - finishedCount: total count of completed pages.
 * 
 * Persist to files:
 * - all urls that have been seen, persist in the same order as been seen.
 *   Always append new url to the file. Use BufferedWriter for better performance.
 * - finishedCount.
 *   Store only one integer. Update as a whole. Use FileWriter.
 * 
 * If the crawler crashed in the middle, in next run it can resume from what left last time.
 * Because urls are scheduled and completed in the same order as persited, based on finishedCount it can quickly
 * allocate the first url that needs to crawl from.
 * 
 */
public class URLFrontier implements IURLFrontier {
  private final String baseDomain;
  private final String startUrl;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  // all seen urls
  private final Set<String> urlSet = ConcurrentHashMap.newKeySet();
  // scheduled urls
  private final Queue<CountDownLatch> urlQueue = new ConcurrentLinkedQueue<>();
  // total counts of completed urls
  volatile int finishedCount = 0;
  volatile boolean completed = false;
  volatile boolean hasError = false;
  private final IHtmlDownloader downloader;

  // persistency
  private final String urlFileName;
  private final String counterFileName;
  private StringListFile urlFile;
  private CounterFile counterFile;


  public URLFrontier(String url, IHtmlDownloader d) {
    downloader = d;
    this.baseDomain = Util.getDomain(url);
    if (baseDomain == null || baseDomain.isEmpty()) {
      throw new IllegalArgumentException("startUrl " + url + " is invalid.");
    }
    if(url.endsWith("/")) {
      url = url.substring(0, url.length()-1);
    }
    startUrl = url;
    urlFileName = baseDomain + ".log";
    counterFileName = baseDomain + ".count";
  }
  @Override
  public void start() throws IOException {
    // init data directory and files
    Path dataDir = Path.of("data");  // relative to project root
    Files.createDirectories(dataDir);  // create if not exists
    boolean newRun = true;
    if (Files.exists(Path.of("data", urlFileName))) {
      newRun = false;
    }
    urlFile = new StringListFile(dataDir.resolve(urlFileName));
    counterFile = new CounterFile(dataDir.resolve(counterFileName));
    
    if(newRun) {
      List<String> list = new ArrayList<>();
      list.add(startUrl);
      add(list);
      System.out.println("Fresh rerun. ");
    } else {
      // resume last run
      List<String> list = urlFile.readAll();
      finishedCount = counterFile.get();
      if(finishedCount == list.size()) {
        throw new IllegalStateException("Last run with domain " 
        + baseDomain + " has already finished. If you want to rerun, remove status files and rerun.");
      }
      urlSet.addAll(list);
      for(int i = finishedCount; i < list.size(); i++) {
        add(list.get(i), true);
      }
      System.out.println("Resume from last run. url size: " + list.size() + ", finishedCount: " + finishedCount);
    }
    executor.submit(()->updateCounter());
  }
  @Override
  public void shutdown() {
    executor.shutdown();
  }
  @Override
  public boolean isCompleted() {
    return completed;
  }
  @Override
  public boolean hasFatalError() {
    return hasError;
  }
  @Override
  public String status() {
    StringBuilder sb = new StringBuilder();
    sb.append("URLFrontier status: \n");
    sb.append("size of urlSet: ").append(urlSet.size()).append("\n");
    sb.append("size of urlQueue: ").append(urlQueue.size()).append("\n");
    sb.append("finishedCount: ").append(finishedCount).append("\n");
    return sb.toString();
  }

  private boolean add(String url, boolean ignoreCheck) {
    if(ignoreCheck || (Util.isSameDomain(url, baseDomain)  && urlSet.add(url))) {
      CountDownLatch latch = new CountDownLatch(2);
      urlQueue.add(latch);
      // new url
      downloader.add(new HtmlPage(url, latch));
      return true;
    }
    return false;
  }
  @Override
  public void add(List<String> urlList) {
    if (hasError) return;
    List<String> newUrlList = new ArrayList<>();
    for(String url : urlList) {
      boolean addOk = add(url, false);
      if(addOk) {
        newUrlList.add(url);
      }
    }

    try {
      urlFile.append(newUrlList);
    } catch(IOException e) {
      e.printStackTrace();
      hasError = true;
    }
  }

  private void updateCounter() {
    try {
      while (!urlQueue.isEmpty()) {
        CountDownLatch latch = urlQueue.remove();
        latch.await();
        finishedCount++;
        while (!urlQueue.isEmpty() && urlQueue.peek().await(0, TimeUnit.SECONDS)) {
          urlQueue.remove();
          finishedCount++;
        }
        counterFile.update(finishedCount);
      }
      completed = true;
    } catch(InterruptedException e) {}
  }
}
