package com.idvp.platform.journal;

import com.idvp.platform.journal.configuration.JournalProviderConfigurator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.MDC;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;

public class JournalTenantsTest extends JournalTestBase {

    @Override
    protected String getJournalFile() {
        return "JournalTenantsTest.journal";
    }

    @Override
    protected String getLogbackConfigFile() {
        return "journal_tenant/logback.xml";
    }

    @Test
    public void testJournalApi() throws Exception {
        System.setProperty(JournalProviderConfigurator.AUTOCONFIG_FILE_PROPERTY, "journal_tenant/journal.config.xml");
        String message1 = "Hello World from first tenant";
        AtomicBoolean tenant1 = new AtomicBoolean(false);
        Thread thread1 = new Thread(() -> {
            MDC.put("tenantId", "firstTenant");
            try {
                Thread.sleep(1000);
                journalProvider.write(message1);
                tenant1.set(true);
            } catch (JournalException | InterruptedException e) {
                e.printStackTrace();
            }
            MDC.clear();
        });

        String message2 = "Hello World from second tenant";
        AtomicBoolean tenant2 = new AtomicBoolean(false);
        Thread thread2 = new Thread(() -> {
            MDC.put("tenantId", "secondTenant");
            try {
                Thread.sleep(1000);
                journalProvider.write(message2);
                tenant2.set(true);
            } catch (JournalException | InterruptedException e) {
                e.printStackTrace();
            }
            MDC.clear();
        });

        thread1.start();
        thread2.start();

        String journalDir1 = getTestPathFor("JournalTenantsTest/firstTenant");
        String journalDir2 = getTestPathFor("JournalTenantsTest/secondTenant");

        await().until(() -> new File(journalDir1).exists() && new File(journalDir2).exists());
        await().until(() -> tenant1.get() && tenant2.get());

        Thread.sleep(4000);
        MDC.put("tenantId", "firstTenant");
        Assert.assertEquals(message1, journalProvider.read(String.class).iterator().next());

        MDC.put("tenantId", "secondTenant");
        Assert.assertEquals(message2, journalProvider.read(String.class).iterator().next());

    }
}
