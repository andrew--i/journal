package com.idvp.platform.journal.reader;

import com.idvp.platform.journal.JournalRecordTransformer;
import com.idvp.platform.journal.reader.collector.LogDataCollector;
import com.idvp.platform.journal.reader.model.LogData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JournalRecordCollector<T> implements LogDataCollector {

  private static final Logger LOGGER = LoggerFactory.getLogger(JournalRecordCollector.class);

  private LogDataCollector logDataCollector;

  private JournalRecordTransformer<T> journalRecordTransformer;

  public JournalRecordCollector(JournalRecordTransformer<T> journalRecordTransformer, LogDataCollector logDataCollector) {
    this.journalRecordTransformer = journalRecordTransformer;
    this.logDataCollector = logDataCollector;
  }


  @Override
  public synchronized void add(LogData... logDatas) {
    LOGGER.info("Collector added log items: " + logDatas.length);
    logDataCollector.add(logDatas);
  }

  @Override
  public synchronized LogData[] getLogData() {
    return logDataCollector.getLogData();
  }

  @Override
  public synchronized int clear() {
    return logDataCollector.clear();
  }

  public Collection<T> getRecords() {
    final LogData[] logData = getLogData();
    List<T> records = Arrays.stream(logData)
        .map(ld -> journalRecordTransformer.fromString(ld.getMessage()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
    return records;
  }
}
