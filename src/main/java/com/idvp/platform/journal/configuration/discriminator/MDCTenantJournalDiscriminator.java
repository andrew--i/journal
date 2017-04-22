package com.idvp.platform.journal.configuration.discriminator;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.MDC;

import java.io.File;

public class MDCTenantJournalDiscriminator extends JournalDiscriminatorDefault {
    private final static String tenantKey = "tenantId";

    @Override
    public String getKey() {
        return tenantKey;
    }

    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        return MDC.get("tenantId") + File.separator + super.getDiscriminatingValue(iLoggingEvent);
    }

    @Override
    public String getJournalDiscriminatingValueByRecord(Object record) {
        final String discriminatorValue = MDC.get("tenantId");
        return discriminatorValue + File.separator + getJournalKeyByRecord(tenantKey, discriminatorValue, record);
    }

    @Override
    public String getJournalDiscriminatingValueByClass(Class<?> journalRecordClass) {
        final String discriminatorValue = MDC.get("tenantId");
        return MDC.get("tenantId") + File.separator + getJournalKeyByClass(tenantKey, discriminatorValue, journalRecordClass);
    }

    @Override
    public String getJournalDiscriminatingValueByJournalKey(String journalKey) {
        return MDC.get("tenantId") + File.separator + journalKey;
    }
}
