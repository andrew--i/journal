package com.idvp.platform.journal;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JournalTest extends JournalTestBase {


  @Override
  protected String getJournalFile() {
    return "sample.journal.txt";
  }


  @Test
  public void testJournalApi() throws Exception {
    System.setProperty(JournalFactory.AUTOCONFIG_FILE_PROPERTY, "journal/journal.config.xml");
    try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("journal/logback.xml")) {
      LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
      context.reset();
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(context);
      configurator.doConfigure(inputStream);
    }

    Journal<String> journal = journalFactory.get("other_journal");
    assertNotNull(journal);
    String message = "some record at " + System.currentTimeMillis();
    journal.write(message);

    await().until(() -> journal.read().isPresent());
    assertEquals(message, journal.read().get());
  }
}