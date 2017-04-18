package com.idvp.platform.journal;

import com.idvp.platform.journal.configuration.JournalFactoryConfigurator;
import org.junit.Test;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JournalTest extends JournalTestBase {


  @Override
  protected String getJournalFile() {
    return "JournalTest.journal.txt";
  }

  @Override
  protected String getLogbackConfigFile() {
    return "journal/logback.xml";
  }

  @Test
  public void testJournalApi() throws Exception {
    System.setProperty(JournalFactoryConfigurator.AUTOCONFIG_FILE_PROPERTY, "journal/journal.config.xml");
    Journal<String> journal = journalFactory.get("other_journal");
    assertNotNull(journal);
    String message = "some record at " + System.currentTimeMillis();
    journal.write(message);

    await().until(() -> journal.read().iterator().hasNext());
    assertEquals(message, journal.read().iterator().next());
  }
}