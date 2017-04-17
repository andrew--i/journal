package com.idvp.platform.journal.appender;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import com.idvp.platform.journal.JournalException;

public interface JournalAppender<T> extends ContextAware, LifeCycle {
  void doAppend(T record) throws JournalException;

  String getName();

  void setName(String name);
}
