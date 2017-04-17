package com.idvp.platform.journal;

import com.idvp.platform.journal.configuration.JournalFactoryConfigurator;
import org.junit.Test;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

public class JournalFactoryReadWriteTest extends JournalTestBase {


  @Override
  protected String getJournalFile() {
    return "target/tests/JournalFactoryReadWriteTest.journal.txt";
  }

  @Override
  protected String getLogbackConfigFile() {
    return "journal/logback.xml";
  }

  @Test
  public void testJournalFactoryReadWrite() throws Exception {
    System.setProperty(JournalFactoryConfigurator.AUTOCONFIG_FILE_PROPERTY, "journal/journal.config.xml");

    String message = "some record at " + System.currentTimeMillis();
    journalFactory.write(message);

    await().until(() -> !journalFactory.read("other_journal").isEmpty());

    assertEquals(message, journalFactory.read("other_journal").iterator().next());
    assertEquals(message, journalFactory.read(String.class).iterator().next());
  }
}