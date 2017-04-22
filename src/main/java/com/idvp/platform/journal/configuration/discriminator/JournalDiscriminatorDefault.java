package com.idvp.platform.journal.configuration.discriminator;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.Discriminator;
import com.idvp.platform.journal.Journal;
import com.idvp.platform.journal.JournalProvider;

import java.util.Map;

public class JournalDiscriminatorDefault implements Discriminator<ILoggingEvent> {

    public static final String journalKey = "com.idvp.platform.journal.key";
    public static final String defaultValue = "journal";

    protected JournalProvider journalProvider;
    private boolean isStarted;

    public void setJournalProvider(JournalProvider journalProvider) {
        this.journalProvider = journalProvider;
    }

    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
//        be careful journalProvider there is always null
        final Map<String, String> mdcPropertyMap = iLoggingEvent.getMDCPropertyMap();
        if (!mdcPropertyMap.containsKey(journalKey))
            return defaultValue;
        return mdcPropertyMap.get(journalKey);
    }

    @Override
    public String getKey() {
        return journalKey;
    }

    public String getJournalDiscriminatingValueByRecord(Object record) {
        return getJournalKeyByRecord(journalKey, defaultValue, record);
    }

    public String getJournalDiscriminatingValueByClass(Class<?> journalRecordClass) {
        return getJournalKeyByClass(journalKey, defaultValue, journalRecordClass);
    }

    public String getJournalDiscriminatingValueByJournalKey(String journalKey) {
        return journalKey;
    }

    protected String getJournalKeyByRecord(String key, String discriminatorValue, Object record) {
        final Journal journal = journalProvider.getByRecord(key, discriminatorValue, record);
        if (journal == null)
            throw new IllegalArgumentException("Could not find journal by discriminator value " + discriminatorValue);
        return journal.getKey();
    }

    protected String getJournalKeyByClass(String key, String discriminatorValue, Class<?> aClass) {
        final Journal journal = journalProvider.getByClass(key, discriminatorValue, aClass);
        if (journal == null)
            throw new IllegalArgumentException("Could not find journal by discriminator value " + discriminatorValue);
        return journal.getKey();
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
