package com.idvp.platform.journal;

import ch.qos.logback.core.joran.spi.JoranException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class JournalRotationTest extends JournalTestBase {

  @Override
  protected String getJournalFile() {
    return "JournalRotationTest/JournalRotationTest.journal";
  }

  @Override
  protected String getLogbackConfigFile() {
    return "journal_rotation/logback.xml";
  }

  @Override
  protected void configureLogging() throws IOException, JoranException {
    String journalDir = getTestPathFor("JournalRotationTest");
    System.setProperty("JOURNAL_DIRECTORY_PATH", journalDir);
    super.configureLogging();
  }



  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    String journalDir = getTestPathFor("JournalRotationTest");
    final File directory = new File(journalDir);
    FileUtils.cleanDirectory(directory);
    FileUtils.deleteDirectory(directory);
  }

  @Override
  protected String getJournalConfigPath() {
    return "journal_rotation/journal.config.xml";
  }

  @Test
  public void testJournalApi() throws Exception {
    String message = "some record at " + System.currentTimeMillis();
    for (int i = 0; i < 30; i++) {
      journalProvider.write(message + "_" + i);
      Thread.sleep(200);
    }


    Thread.sleep(2000);
    final Collection<String> records = journalProvider.read(String.class);
    assertFalse(records.isEmpty());
    // see journal.config.xml
    assertEquals(20, records.size());

    assertEquals(message + "_29", records.iterator().next());


  }
}
