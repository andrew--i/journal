package com.idvp.platform.journal.configuration.discriminator;

import ch.qos.logback.classic.sift.MDCBasedDiscriminator;
import org.slf4j.MDC;

public class MDCTenantJournalDiscriminator extends MDCBasedDiscriminator implements JournalDiscriminator {

    public MDCTenantJournalDiscriminator() {
        setDefaultValue(null);
        setKey("tenantId");
    }

    @Override
    public String getJournalDiscriminatingValueByRecord(Object record) {
        return MDC.get("tenantId");
    }

    @Override
    public String getJournalDiscriminatingValueByClass(Class<?> journalRecordClass) {
        return MDC.get("tenantId");
    }

    @Override
    public String getJournalDiscriminatingValueByJournalKey(String journalKey) {
        return MDC.get("tenantId");
    }
}
