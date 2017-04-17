package com.idvp.platform.journal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JournalTest {

  private static String journalTest = "sample.journal.txt";


  @Before
  public void setUp() throws Exception {
    getFile().delete();
    getFile().createNewFile();
    System.setProperty("JOURNAL_FILE_PATH", getFile().getAbsolutePath());
  }

  @After
  public void tearDown() throws Exception {
    JournalFactory.shutdown();
    boolean delete = getFile().delete();
    if (!delete)
      System.out.println("Please, delete " + getFile().getAbsolutePath());
  }

  private File getFile() {
    String filePath = new File(".").getAbsolutePath() + File.separatorChar + journalTest;
    return new File(filePath);
  }

  @Test
  public void testJournalApi() throws Exception {
    System.setProperty(JournalFactory.AUTOCONFIG_FILE_PROPERTY, "journal/journal.config.xml");
    System.setProperty("logback.configurationFile", "journal/logback.xml");

    Journal<String> journal = JournalFactory.get("one");
    journal.open();
    assertNotNull(journal);
    String message = "some record at " + System.currentTimeMillis();
    journal.write(message);

    await().until(() -> journal.read().isPresent());
    assertEquals(message, journal.read().get());


  }
}