package com.idvp.platform.journal;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JournalConcurrencyTest extends JournalTestBase {

    private JournalProvider journalFactory1;
    private JournalProvider journalFactory2;

    @Override
    protected String getJournalFile() {
        return "JournalConcurrencyTest/string_journal";
    }

    @Override
    protected String getLogbackConfigFile() {
        return "journal_concurrency/logback.xml";
    }

    @Test
    public void testJournalApi() throws Exception {
        journalFactory1 = new JournalProvider("journal_concurrency/journal_1.config.xml");
        journalFactory2 = new JournalProvider("journal_concurrency/journal_2.config.xml");

        String message1 = "some record at " + System.currentTimeMillis();
        journalFactory1.write(message1);
        String message2 = message1 + "_2";
        journalFactory2.write(message2);


        Thread.sleep(2100);

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
