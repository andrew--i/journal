package com.idvp.platform.journal.configuration.discriminator;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.Discriminator;
import com.idvp.platform.journal.JournalProvider;

public class JournalDiscriminatorDefault implements Discriminator<ILoggingEvent> {

    public static final String journalKey = "com.idvp.platform.journal.key";
    public static final String defaultValue = "journal";

    private boolean isStarted;

    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
//        be careful journalProvider there is always null
        Object[] argumentArray = iLoggingEvent.getArgumentArray();
        if (argumentArray == null || argumentArray.length == 0)
            return defaultValue;
        if (!(argumentArray[0] instanceof String))
            return defaultValue;
        return argumentArray[0].toString();
    }

    @Override
    public String getKey() {
        return journalKey;
    }

    public String getJournalDiscriminatingValueByRecord(Object record) {
        return defaultValue;
    }

    public String getJournalDiscriminatingValueByClass(Class<?> journalRecordClass) {
        return defaultValue;
    }

    public String getJournalDiscriminatingValueByJournalKey(String journalKey) {
        return journalKey;
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
