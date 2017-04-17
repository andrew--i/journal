package com.idvp.platform.journal;

import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.Loader;
import com.idvp.platform.journal.configuration.joran.JoranConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class JournalFactory extends ContextBase implements LifeCycle {


  private static Logger logger = LoggerFactory.getLogger(JournalFactory.class);


  public static final String AUTOCONFIG_FILE = "idvp.platform.journal.xml";
  public static final String AUTOCONFIG_FILE_PROPERTY = "idvp.platform.journal.config.file";

  private Map<String, Journal> journals = new HashMap<>();


  public void lazyInit() {
    try {
      init();
    } catch (JournalException e) {
      stop();
      logger.error("Could not initialize journal factory");
    }
  }

  private void init() throws JournalException {
    autoConfig(this, Loader.getTCL());
  }

  public <T> Journal<T> get(String key) {
    if (!isStarted()) {
      lazyInit();
      start();
    }
    return journals.get(key);
  }


  private static void autoConfig(JournalFactory journalFactory, ClassLoader classLoader) throws JournalException {

    String autoConfigFileByProperty = System.getProperty(AUTOCONFIG_FILE_PROPERTY);
    URL url;

    if (autoConfigFileByProperty != null) {
      url = Loader.getResource(autoConfigFileByProperty, classLoader);
    } else {
      url = Loader.getResource(AUTOCONFIG_FILE, classLoader);
    }
    if (url != null) {
      configureByResource(journalFactory, url);
    } else {
      String errMsg;
      if (autoConfigFileByProperty != null) {
        errMsg = "Failed to find configuration file [" + autoConfigFileByProperty + "].";
      } else {
        errMsg = "Failed to find logback-audit configuration files  [" + AUTOCONFIG_FILE + "].";
      }
      throw new JournalException(errMsg);
    }
  }

  private static void configureByResource(JournalFactory context, URL url) throws JournalException {
    JoranConfiguration configurator = new JoranConfiguration();
    configurator.setContext(context);

    try {
      configurator.doConfigure(url);
    } catch (JoranException e) {
      throw new JournalException("Configuration failure in " + url, e);
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
}
