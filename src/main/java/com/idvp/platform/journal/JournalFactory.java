package com.idvp.platform.journal;

import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.Loader;
import com.idvp.platform.journal.configuration.JournalFactoryConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JournalFactory extends ContextBase implements LifeCycle {


  private static Logger logger = LoggerFactory.getLogger(JournalFactory.class);
  private Map<String, Journal> journals = new HashMap<>();


  public void lazyInit() {
    try {
      new JournalFactoryConfigurator().autoConfig(this, Loader.getTCL());
    } catch (JournalException e) {
      stop();
      logger.error("Could not initialize journal factory");
    }
  }

  public <T> Journal<T> get(String key) {
    checkStartFactory();
    return journals.getOrDefault(key, null);
  }

  private synchronized void checkStartFactory() {
    if (!isStarted()) {
      lazyInit();
      start();
    }
  }


  public void addJournal(Journal journal) {
    journals.putIfAbsent(journal.getKey(), journal);
  }

  @Override
  public void start() {
    super.start();
    for (Journal journal : journals.values()) {
      journal.getJournalRecordsReader().open();
    }
  }

  @Override
  public void stop() {
    super.stop();
    for (Journal journal : journals.values()) {
      journal.close();
    }
    journals.clear();
  }


  public void write(Object record) throws JournalException {
    checkStartFactory();
    for (Journal journal : journals.values()) {
      if (journal.getTClass().getName().equalsIgnoreCase(record.getClass().getName()))
        journal.write(record);
    }
  }

  public <T> Collection<T> read(Class<T> tClass) {
    checkStartFactory();
    return (Collection<T>) journals.values().stream()
        .filter(j -> j.getTClass().getName().equalsIgnoreCase(tClass.getName()))
        .flatMap(j -> j.<T>read().stream())
        .collect(Collectors.toList());
  }

  public <T> Collection<T> read(String key) {
    Journal journal = get(key);
    if (journal == null)
      return Collections.emptyList();
    return journal.read();
  }

}
