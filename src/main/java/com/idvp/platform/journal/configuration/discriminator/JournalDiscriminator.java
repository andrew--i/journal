package com.idvp.platform.journal.configuration.discriminator;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.Discriminator;

public interface JournalDiscriminator extends Discriminator<ILoggingEvent> {


    String getJournalDiscriminatingValueByRecord(Object record);

    String getJournalDiscriminatingValueByClass(Class<?> journalRecordClass);

    String getJournalDiscriminatingValueByJournalKey(String journalKey);
}
