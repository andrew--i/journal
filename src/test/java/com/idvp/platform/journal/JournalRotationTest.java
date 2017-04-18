package com.idvp.platform.journal;

import ch.qos.logback.core.joran.spi.JoranException;
import com.idvp.platform.journal.configuration.JournalFactoryConfigurator;
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

  @Test
  public void testJournalApi() throws Exception {
    System.setProperty(JournalFactoryConfigurator.AUTOCONFIG_FILE_PROPERTY, "journal_rotation/journal.config.xml");
    String message = "some record at " + System.currentTimeMillis();
    for (int i = 0; i < 60; i++) {
      journalFactory.write(message + "_" + i);
      Thread.sleep(20);
    }


    Thread.sleep(2000);
    final Collection<String> records = journalFactory.read(String.class);
    assertFalse(records.isEmpty());
    assertEquals(60, records.size());

    assertEquals(message + "_59", records.iterator().next());


  }
}
