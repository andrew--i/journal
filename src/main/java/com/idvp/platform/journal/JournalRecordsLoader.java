package com.idvp.platform.journal;

import com.idvp.platform.importer.LogImporterUsingParser;
import com.idvp.platform.journal.configuration.JournalConfigurationProvider;
import com.idvp.platform.loader.BasicLogLoader;
import com.idvp.platform.loading.Source;

public class JournalRecordsLoader<T> {
	private final BasicLogLoader basicLoader;
	private Source source;
	private JournalRecordCollector<T> journalRecordCollector;
	private JournalRecordTransformer<T> transformer;

	public JournalRecordsLoader(String key, JournalRecordCollector<T> journalRecordCollector, JournalRecordTransformer<T> transformer) {
		this.journalRecordCollector = journalRecordCollector;
		this.transformer = transformer;
		this.basicLoader = new BasicLogLoader();
		new JournalConfigurationProvider().createSourceByJournalKey(key).ifPresent(s -> source = s);

	}

	public void open() {
		if (source == null)
			throw new IllegalArgumentException("Could not open journal with nullable source");
		basicLoader.startLoading(source, new LogImporterUsingParser(transformer), journalRecordCollector);

	}

	public void close() {
		basicLoader.shutdown();
	}
}
