package com.web.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiConsumer;

public class ThreadPoolTaskExecutor implements TaskExecutor {
  private final ExecutorService executor;

  public ThreadPoolTaskExecutor(int poolSize) {
    ThreadFactory factory = Thread.ofPlatform().name("TP_Downloader-", 0).factory();
    executor = Executors.newFixedThreadPool(poolSize, factory);
  }

  @Override
  public <T,U> void submit(BiConsumer<T,U> consumer, T t, U u) {
    executor.submit(()->consumer.accept(t, u));
  }
  @Override
  public void shutdown() {
    executor.shutdown();
  }
}
