package com.web.executor;

public class ExecutorFactory {
  public static TaskExecutor createExecutor(int poolSize, boolean useVirtualThread) {
    if(useVirtualThread) {
      return new VirtualThreadTaskExecutor(poolSize);
    } else {
      return new ThreadPoolTaskExecutor(poolSize);
    }

  }
}
