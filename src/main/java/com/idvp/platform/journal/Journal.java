package com.idvp.platform.journal;

import com.idvp.platform.journal.appender.JournalAppender;
import com.idvp.platform.journal.reader.JournalRecordsReader;

import java.util.Collection;

public class Journal<T> {

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

  public void setJournalRecordsReader(JournalRecordsReader<T> journalRecordsReader) {
    this.journalRecordsReader = journalRecordsReader;
  }

  public JournalRecordsReader<T> getJournalRecordsReader() {
    return journalRecordsReader;
  }

  public Journal(String key, Class<T> tClass) {
    this.key = key;
    this.tClass = tClass;
  }

  public void close() {
    journalRecordsReader.close();
    journalRecordAppender.stop();
  }


  public void write(T record) throws JournalException {
    journalRecordAppender.doAppend(record);
  }

  public Collection<T> read() {
    return this.journalRecordsReader.getJournalRecordCollector().getRecords();
  }
}
