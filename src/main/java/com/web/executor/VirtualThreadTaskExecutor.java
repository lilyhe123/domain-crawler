package com.web.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

public class VirtualThreadTaskExecutor implements TaskExecutor {
  private final Semaphore semaphore;
  private final ExecutorService executor;

  public VirtualThreadTaskExecutor(int permits) {
    semaphore = new Semaphore(permits);
    ThreadFactory virtualNamedFactory = Thread.ofVirtual().name("VT_Downloader-", 0).factory();
    executor = Executors.newThreadPerTaskExecutor(virtualNamedFactory);
  }

  @Override
  public <T, U> void submit(Consumer<T> consumer, T t) {
    executor.submit(() -> {
      try {
        semaphore.acquire();
        try {
          consumer.accept(t);
        } finally {
          semaphore.release();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });
  }

  @Override
  public void shutdown() {
    executor.shutdown();
  }

}
