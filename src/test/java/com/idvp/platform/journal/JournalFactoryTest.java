package com.idvp.platform.journal;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class JournalFactoryTest {


  @Test
  public void testOneJournalConfiguration() throws Exception {
    System.setProperty(JournalFactory.AUTOCONFIG_FILE_PROPERTY, "factory/one.journal.config.xml");
    JournalFactory.lazyInit();

    Journal<String> journal = JournalFactory.get("one");
    assertNotNull(journal);

    assertNotNull(journal.getTClass());
    assertNotNull(journal.getJournalAppender());
    assertNotNull(journal.getSource());
  }

  @Test
  public void testManyJournalConfiguration() throws Exception {
    System.setProperty(JournalFactory.AUTOCONFIG_FILE_PROPERTY, "factory/many.journal.config.xml");
    JournalFactory.lazyInit();

    Journal<String> journal_1 = JournalFactory.get("one");
    assertNotNull(journal_1);
    assertNotNull(journal_1.getTClass());
    assertNotNull(journal_1.getJournalAppender());
    assertNotNull(journal_1.getSource());

    Journal<String> journal_2 = JournalFactory.get("two");
    assertNotNull(journal_2);
    assertNotNull(journal_2.getTClass());
    assertNotNull(journal_2.getJournalAppender());
    assertNotNull(journal_2.getSource());

  }
}