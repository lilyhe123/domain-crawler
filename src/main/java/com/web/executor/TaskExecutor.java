package com.web.executor;

import java.util.function.BiConsumer;

public interface TaskExecutor {
  <T,U> void submit(BiConsumer<T,U> consumer, T t, U u);
  void shutdown();
}
