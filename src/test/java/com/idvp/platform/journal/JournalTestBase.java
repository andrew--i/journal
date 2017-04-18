package com.idvp.platform.journal;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.apache.commons.vfs2.VFS;
import org.junit.After;
import org.junit.Before;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JournalTestBase {

  protected JournalProvider journalProvider;

  protected String getJournalFile() {
    return "sample.file";
  }


  protected File getJournalPath() {
    String filePath = getTestPathFor(getJournalFile());
    return new File(filePath);
  }

  protected String getLogbackConfigFile() {
    return null;
  }

  protected String getTestPathFor(String file) {
    String filePath = new File(".").getAbsolutePath() + File.separatorChar + "target/tests/" + file;
    return filePath;
  }

  protected void configureLogging() throws IOException, JoranException {
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
    System.setProperty("JOURNAL_FILE", getJournalFile());
    configureLogging();
    journalProvider = new JournalProvider();
  }

  @After
  public void tearDown() throws Exception {

    //close journals and associated resources
    if (journalProvider != null)
      journalProvider.stop();

    //close logback and associated resources
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    context.stop();

    //close cache vfs and associated resources
    VFS.getManager().getFilesCache().close();

    //delete journal file
    if (getJournalPath().exists()) {
      boolean delete = getJournalPath().delete();
      if (!delete)
        System.out.println("Please, delete " + getJournalPath().getAbsolutePath());
    }
  }
}
