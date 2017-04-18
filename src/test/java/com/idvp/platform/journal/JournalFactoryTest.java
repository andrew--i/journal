package com.idvp.platform.journal;

import com.idvp.platform.journal.configuration.JournalFactoryConfigurator;
import com.idvp.platform.journal.configuration.JournalProviderConfigurator;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class JournalFactoryTest extends JournalTestBase {


  @Test
  public void testOneJournalConfiguration() throws Exception {
    System.setProperty(JournalProviderConfigurator.AUTOCONFIG_FILE_PROPERTY, "factory/one.journal.config.xml");

    Journal<String> journal = journalFactory.get("one");
    assertNotNull(journal);

    assertNotNull(journal.getTClass());
    assertNotNull(journal.getJournalRecordAppender());
    assertNotNull(journal.getJournalRecordsReader());
  }

  @Test
  public void testManyJournalConfiguration() throws Exception {
    System.setProperty(JournalProviderConfigurator.AUTOCONFIG_FILE_PROPERTY, "factory/many.journal.config.xml");

    Journal<String> journal_1 = journalFactory.get("one");
    assertNotNull(journal_1);
    assertNotNull(journal_1.getTClass());
    assertNotNull(journal_1.getJournalRecordAppender());
    assertNotNull(journal_1.getJournalRecordsReader());

    Journal<String> journal_2 = journalFactory.get("two");
    assertNotNull(journal_2);
    assertNotNull(journal_2.getTClass());
    assertNotNull(journal_2.getJournalRecordAppender());
    assertNotNull(journal_2.getJournalRecordsReader());

  }
}