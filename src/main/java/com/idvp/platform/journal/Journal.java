package com.idvp.platform.journal;

import ch.qos.logback.core.spi.LifeCycle;
import com.idvp.platform.journal.appender.JournalAppender;
import com.idvp.platform.journal.reader.JournalRecordsReader;

import java.util.Collection;

public class Journal<T> implements LifeCycle {

    /**
     * Ключ журнала, уникальный в рамках всех журналов
     */
    private String key;
    /**
     * Класс записи журнала
     */
    private Class<T> tClass;

    /**
     * Загрузчик записей журнала
     */
    private JournalRecordsReader<T> journalRecordsReader;

    private JournalAppender<T> journalRecordAppender;
    private boolean isStarted;

    public Journal(String key, Class<T> tClass) {
        this.key = key;
        this.tClass = tClass;
    }

    public String getKey() {
        return key;
    }

    public Class<T> getTClass() {
        return tClass;
    }

    public JournalAppender<T> getJournalRecordAppender() {
        return journalRecordAppender;
    }

    public void setJournalRecordAppender(JournalAppender journalRecordAppender) {
        this.journalRecordAppender = journalRecordAppender;
    }

    public JournalRecordsReader<T> getJournalRecordsReader() {
        return journalRecordsReader;
    }

    public void setJournalRecordsReader(JournalRecordsReader<T> journalRecordsReader) {
        this.journalRecordsReader = journalRecordsReader;
    }

    public void write(T record) throws JournalException {
        if (!isStarted)
            start();
        journalRecordAppender.doAppend(key, record);
    }

    public Collection<T> read() {
        if (!isStarted)
            start();
        return this.journalRecordsReader.getJournalRecordCollector().getRecords();
    }

    @Override
    public void start() {
        journalRecordAppender.start();
        journalRecordsReader.open();
        isStarted = true;
    }

    @Override
    public void stop() {
        journalRecordAppender.stop();
        journalRecordsReader.close();
        isStarted = false;
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }
}
