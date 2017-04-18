package com.idvp.platform.journal;

import com.idvp.platform.journal.configuration.JournalProviderConfigurator;
import org.junit.Test;

import java.io.File;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JournalFolderTest extends JournalTestBase {

  @Override
  protected String getJournalFile() {
    return "JournalFolderTest/JournalFolderTest.journal";
  }

  @Override
  protected String getLogbackConfigFile() {
    return "journal_folder/logback.xml";
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    String journalDir = getTestPathFor("JournalFolderTest");
    System.setProperty("JOURNAL_DIRECTORY_PATH", journalDir);
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    String journalDir = getTestPathFor("JournalFolderTest");
    new File(journalDir).delete();
  }

  @Test
  public void testJournalApi() throws Exception {
    System.setProperty(JournalProviderConfigurator.AUTOCONFIG_FILE_PROPERTY, "journal_folder/journal.config.xml");
    Journal<String> journal = journalFactory.get("string_journal");
    assertNotNull(journal);
    String message = "some record at " + System.currentTimeMillis();
    journal.write(message);

    await().until(() -> journal.read().iterator().hasNext());
    assertEquals(message, journal.read().iterator().next());
  }
}
