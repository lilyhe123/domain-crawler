package com.web.threadpool;

public class Consts {
  // constants for DocDownloader
  public static final int HTTP_GET_TIMEOUT = 10000;// 10 secs
  public static final int MAX_RETRIES = 3;
  public static final long BASE_RETRY_DELAY = 1000; // 1 second
  public static final long DOWNLOAD_DELAY = 5;

  // constants of thread pool and task queue
  public static final int QUEUE_CAPACITY = 500;
  public static final int DOWNLOAD_POOL_SIZE = 10;
  public static final int PARSER_POOL_SIZE = 2;
  public static final int CONSUMER_POOL_SIZE = 1;
}
