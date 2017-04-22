package com.idvp.platform.journal.appender;

import com.idvp.platform.journal.JournalRecordTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class SLF4JJournalAppender<T> extends JournalAppenderBase<T> {

    private JournalRecordTransformer<T> recordTransformer;
    private String loggerName;

    public SLF4JJournalAppender(SLF4jJournalAppenderParameter<T> parameter) {
        recordTransformer = new JournalRecordTransformer<>(parameter.tClass);
        this.loggerName = parameter.loggerName;
    }

    @Override
    protected void append(String key, T record) {
        Optional<String> message = recordTransformer.toString(record);
        message.ifPresent(m -> {
            Logger logger = LoggerFactory.getLogger(loggerName);
            logger.info(m, key);
        });
    }

    public static class SLF4jJournalAppenderParameter<T> {
        public Class<T> tClass;
        public String loggerName;
    }
}
