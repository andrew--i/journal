package com.idvp.platform.journal;

import org.junit.After;
import org.junit.Before;

import java.io.File;

public class JournalTestBase {

  protected JournalFactory journalFactory;

  protected String getJournalFile() {
    return "sample.file";
  }


  protected File getJournalPath() {
    String filePath = new File(".").getAbsolutePath() + File.separatorChar + getJournalFile();
    return new File(filePath);
  }


  @Before
  public void setUp() throws Exception {
    File journalPath = getJournalPath();
    journalPath.delete();
//    journalPath.createNewFile();
    System.setProperty("JOURNAL_FILE_PATH", journalPath.toString());

    journalFactory = new JournalFactory();
  }

  @After
  public void tearDown() throws Exception {
    journalFactory.stop();
    if (getJournalPath().exists()) {
      boolean delete = getJournalPath().delete();
      if (!delete)
        System.out.println("Please, delete " + getJournalPath().getAbsolutePath());
    }
  }
}
