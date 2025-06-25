package com.web.executor;

import java.util.function.Consumer;

public interface TaskExecutor {
  <T,U> void submit(Consumer<T> consumer, T t);
  void shutdown();
}
