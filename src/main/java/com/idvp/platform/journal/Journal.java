package com.idvp.platform.journal;

import com.idvp.platform.journal.reader.JournalRecordsLoader;
import com.idvp.platform.journal.reader.loading.Source;
import com.idvp.platform.journal.writer.JournalAppender;

import java.util.Optional;

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
  private JournalRecordsLoader<T> journalRecordsLoader;

  /**
   * Источник, откуда загружать логи
   */
  private Source source;

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

  public Source getSource() {
    return source;
  }


  public Journal(String key, Class<T> tClass, Source source) {
    this.key = key;
    this.tClass = tClass;
    this.source = source;
    this.journalRecordsLoader = new JournalRecordsLoader<>(source, tClass);
  }

  public void open() {
    this.journalRecordsLoader.open();

  }

  public void close() {
    journalRecordsLoader.close();
    journalRecordAppender.stop();
  }


  public void write(T record) throws JournalException {
    journalRecordAppender.doAppend(record);
  }


  public Optional<T> read() {
    return this.journalRecordsLoader.getJournalRecordCollector().getRecord();
  }
}
