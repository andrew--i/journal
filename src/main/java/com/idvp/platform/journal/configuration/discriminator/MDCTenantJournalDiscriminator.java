package com.idvp.platform.journal.configuration.discriminator;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.MDC;

public class MDCTenantJournalDiscriminator extends JournalDiscriminatorDefault {
    private final static String tenantKey = "tenantId";

    @Override
    public String getKey() {
        return tenantKey;
    }

    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        return getTenantValue() + "/" + super.getDiscriminatingValue(iLoggingEvent);
    }

    @Override
    public String getJournalDiscriminatingValueByRecord(Object record) {
        return getTenantValue();
    }

    @Override
    public String getJournalDiscriminatingValueByClass(Class<?> journalRecordClass) {
        return getTenantValue();
    }

    @Override
    public String getJournalDiscriminatingValueByJournalKey(String journalKey) {
        return getTenantValue();
    }

    private String getTenantValue() {
        String value = MDC.get(tenantKey);
        return value == null ? JournalDiscriminatorDefault.defaultValue : value;
    }
}
