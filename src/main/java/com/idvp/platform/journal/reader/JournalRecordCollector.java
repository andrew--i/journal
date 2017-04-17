package com.idvp.platform.journal.reader;

import com.idvp.platform.journal.JournalRecordTransformer;
import com.idvp.platform.journal.reader.collector.LogDataCollector;
import com.idvp.platform.journal.reader.model.LogData;

import java.util.Optional;

public class JournalRecordCollector<T> implements LogDataCollector {

  private LogDataCollector logDataCollector;

  private JournalRecordTransformer<T> journalRecordTransformer;

  public JournalRecordCollector(JournalRecordTransformer<T> journalRecordTransformer, LogDataCollector logDataCollector) {
    this.journalRecordTransformer = journalRecordTransformer;
    this.logDataCollector = logDataCollector;
  }

  public Optional<T> getRecord() {
    final LogData[] logData = logDataCollector.getLogData();
    if (logData.length == 0)
      return Optional.empty();
    final LogData lastRecord = logData[logData.length - 1];
    return journalRecordTransformer.fromString(lastRecord.getMessage());
  }

  @Override
  public void add(LogData... logDatas) {
    logDataCollector.add(logDatas);
  }

  @Override
  public LogData[] getLogData() {
    return logDataCollector.getLogData();
  }

  @Override
  public int clear() {
    return logDataCollector.clear();
  }
}
