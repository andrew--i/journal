package com.idvp.platform.journal.configuration.discriminator;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class NoOpJournalDiscriminator implements JournalDiscriminator {
    private static String value = "default";
    private static String key = "";
    private boolean isStarted;

    @Override
    public String getJournalDiscriminatingValueByRecord(Object record) {
        return value;
    }

    @Override
    public String getJournalDiscriminatingValueByClass(Class<?> journalRecordClass) {
        return value;
    }

    @Override
    public String getJournalDiscriminatingValueByJournalKey(String journalKey) {
        return value;
    }

    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        return value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void start() {
        isStarted = true;
    }

    @Override
    public void stop() {
        isStarted = false;
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }
}
