package com.idvp.platform.journal;

import com.idvp.platform.journal.reader.JournalRecordCollector;
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
   * Собирает записи журнала
   */
  private JournalRecordCollector<T> journalRecordCollector;

  /**
   * Загрузчик записей журнала
   */
  private JournalRecordsLoader<T> journalRecordsLoader;

  /**
   * Источник, откуда загружать логи
   */
  private Source source;

  private JournalAppender<T> journalAppender;

  public String getKey() {
    return key;
  }

  public Class<T> getTClass() {
    return tClass;
  }

  public JournalAppender<T> getJournalAppender() {
    return journalAppender;
  }

  public void setJournalAppender(JournalAppender journalAppender) {
    this.journalAppender = journalAppender;
  }

  public Source getSource() {
    return source;
  }


  public Journal(String key, Class<T> tClass, Source source) {
    this.key = key;
    this.tClass = tClass;
    this.source = source;
    JournalRecordTransformer<T> transformer = new JournalRecordTransformer<>(tClass);
    this.journalRecordCollector = new JournalRecordCollector<>(transformer);
    this.journalRecordsLoader = new JournalRecordsLoader<>(source, this.journalRecordCollector, transformer);
  }

  public void open() {
    this.journalRecordsLoader.open();

  }

  public void close() {
    journalRecordsLoader.close();
    journalAppender.stop();
  }


  public void write(T record) throws JournalException {
    journalAppender.doAppend(record);
  }


  public Optional<T> read() {
    return journalRecordCollector.getRecord();
  }
}
