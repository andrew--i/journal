package com.idvp.platform.journal;

import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.spi.LifeCycle;
import com.idvp.platform.journal.configuration.JournalFactoryConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JournalFactory extends ContextBase implements LifeCycle {


  private static Logger logger = LoggerFactory.getLogger(JournalFactory.class);
  private Map<String, Journal> journals = new ConcurrentHashMap<>();

  public JournalFactory(String config) {
    try (InputStream inputStream = new ByteArrayInputStream(config.getBytes("UTF-8"))) {
      new JournalFactoryConfigurator().configureByResourceStream(this, inputStream);
    } catch (IOException | JournalException e) {
      logger.error("Could not create journal factory", e);
      e.printStackTrace();
    }
  }

  public <T> Journal<T> get(String key) {
    return journals.getOrDefault(key, null);
  }


  public void addJournal(Journal journal) {
    journals.putIfAbsent(journal.getKey(), journal);
  }

  @Override
  public void start() {
    super.start();
    for (Journal journal : journals.values()) {
      journal.start();
    }
  }

  @Override
  public void stop() {
    super.stop();
    for (Journal journal : journals.values()) {
      journal.stop();
    }
    journals.clear();
  }


  public void write(Object record) throws JournalException {
    for (Journal journal : journals.values()) {
      if (journal.getTClass().getName().equalsIgnoreCase(record.getClass().getName()))
        journal.write(record);
    }
  }

  public <T> Collection<T> read(Class<T> tClass) {
    if(!isStarted())
      start();

    return (Collection<T>) journals.values().stream()
        .filter(j -> j.getTClass().getName().equalsIgnoreCase(tClass.getName()))
        .flatMap(j -> j.<T>read().stream())
        .collect(Collectors.toList());
  }

  public <T> Collection<T> read(String key) {
    if(!isStarted())
      start();
    Journal journal = get(key);
    if (journal == null)
      return Collections.emptyList();
    return journal.read();
  }

}
