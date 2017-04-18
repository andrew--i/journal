package com.idvp.platform.journal;

import com.idvp.platform.journal.configuration.JournalProviderConfigurator;
import org.junit.Test;

import java.util.Collection;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

public class JournalFactoryReadWriteTest extends JournalTestBase {


  @Override
  protected String getJournalFile() {
    return "JournalFactoryReadWriteTest.journal.txt";
  }

  @Override
  protected String getLogbackConfigFile() {
    return "journal/logback.xml";
  }

  @Test
  public void testJournalFactoryReadWrite() throws Exception {
    System.setProperty(JournalProviderConfigurator.AUTOCONFIG_FILE_PROPERTY, "journal/journal.config.xml");

    String message = "some record at " + System.currentTimeMillis();
    journalProvider.write(message);

    Thread.sleep(2100);

    final Collection<Object> r1 = journalProvider.read("other_journal");
    assertEquals(message, r1.iterator().next());
    final Collection<String> r2 = journalProvider.read(String.class);
    assertEquals(message, r2.iterator().next());
  }
}