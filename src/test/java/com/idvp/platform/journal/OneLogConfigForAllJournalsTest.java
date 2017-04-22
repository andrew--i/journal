package com.idvp.platform.journal;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OneLogConfigForAllJournalsTest extends JournalTestBase {


    @Override
    protected String getJournalFile() {
        return "OneLogConfigForAllJournalsTest.journal";
    }

    @Override
    protected String getLogbackConfigFile() {
        return "journal_one_logger/logback.xml";
    }

    @Override
    protected String getJournalConfigPath() {
        return "journal_one_logger/journal.config.xml";
    }

    @Test
    public void testJournalApi() throws Exception {

        String message = "some record at " + System.currentTimeMillis();
        journalProvider.write(message);
        journalProvider.write(Integer.valueOf(10));


        Thread.sleep(3000);
        assertEquals(message, journalProvider.read(String.class).iterator().next());
        assertEquals(Integer.valueOf(10), journalProvider.read(Integer.class).iterator().next());

    }
}
