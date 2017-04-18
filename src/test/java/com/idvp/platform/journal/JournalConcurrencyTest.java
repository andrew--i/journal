package com.idvp.platform.journal;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JournalConcurrencyTest extends JournalTestBase {

  private JournalFactory journalFactory1;
  private JournalFactory journalFactory2;
  private String journalDir = getTestPathFor("JournalConcurrencyTest");

  @Override
  protected String getJournalFile() {
    return "JournalConcurrencyTest/string_journal";
  }

  @Override
  protected String getLogbackConfigFile() {
    return "journal_concurrency/logback.xml";
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    Thread.sleep(10000);
    final File file = new File(journalDir);
    if(file.exists())
      FileUtils.deleteQuietly(file);
    System.setProperty("JOURNAL_DIRECTORY_PATH", journalDir);


  }

  @After
  public void tearDown() throws Exception {
    super.tearDown();
    journalFactory1.stop();
    journalFactory2.stop();
    final File file = new File((journalDir));
    for (File f : file.listFiles()) {
      Files.delete(f.toPath());
    }
    Files.delete(file.toPath());
  }

  @Test
  public void testJournalApi() throws Exception {
    journalFactory1 = new JournalFactory("journal_concurrency/journal_1.config.xml");
    journalFactory2 = new JournalFactory("journal_concurrency/journal_2.config.xml");

    String message1 = "some record at " + System.currentTimeMillis();
    journalFactory1.write(message1);
    String message2 = message1 + "_2";
    journalFactory2.write(message2);



    Thread.sleep(2000);

    Collection<String> messages1 = journalFactory1.read(String.class);
    assertEquals(2, messages1.size());
    assertTrue(messages1.contains(message1));
    assertTrue(messages1.contains(message2));

    Collection<String> messages2 = journalFactory1.read(String.class);
    assertEquals(2, messages2.size());
    assertTrue(messages2.contains(message1));
    assertTrue(messages2.contains(message2));
  }
}
