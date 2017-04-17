package com.idvp.platform.journal.reader;

import com.idvp.platform.journal.JournalRecordTransformer;
import com.idvp.platform.journal.reader.importer.LogImporterUsingParser;
import com.idvp.platform.journal.reader.loader.BasicLogLoader;
import com.idvp.platform.journal.reader.loading.Source;
import com.idvp.platform.journal.reader.parser.LogParser;

public class JournalRecordsLoader<T> {
  private final BasicLogLoader basicLoader;
  private Source source;
  private JournalRecordCollector<T> journalRecordCollector;
  private LogParser logParser;

  public JournalRecordsLoader(Source source, JournalRecordCollector<T> journalRecordCollector, JournalRecordTransformer<T> transformer) {
    this.journalRecordCollector = journalRecordCollector;
    this.logParser = new JournalRecordParser(transformer);

    this.basicLoader = new BasicLogLoader();
    this.source = source;

  }

  public void open() {
    if (source == null)
      throw new IllegalArgumentException("Could not open journal with nullable source");
    basicLoader.startLoading(source, new LogImporterUsingParser(logParser), journalRecordCollector);

  }

  public void close() {
    basicLoader.shutdown();
  }
}
