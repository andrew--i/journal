package com.idvp.platform.journal;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.After;
import org.junit.Before;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JournalTestBase {

  protected JournalFactory journalFactory;

  protected String getJournalFile() {
    return "sample.file";
  }


  protected File getJournalPath() {
    String filePath = new File(".").getAbsolutePath() + File.separatorChar + getJournalFile();
    return new File(filePath);
  }

  protected String getLogbackConfigFile() {
    return null;
  }

  private void configureLogging() throws IOException, JoranException {
    String logbackConfigFile = getLogbackConfigFile();
    if (logbackConfigFile != null)
      try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(logbackConfigFile)) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(inputStream);
      }
  }

  @Before
  public void setUp() throws Exception {
    File journalPath = getJournalPath();
    journalPath.delete();
    System.setProperty("JOURNAL_FILE_PATH", journalPath.toString());
    configureLogging();
    journalFactory = new JournalFactory();
  }

  @After
  public void tearDown() throws Exception {
    journalFactory.stop();
    if (getJournalPath().exists()) {
      boolean delete = getJournalPath().delete();
      if (!delete)
        System.out.println("Please, delete " + getJournalPath().getAbsolutePath());
    }
  }
}
