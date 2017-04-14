package com.idvp.platform.journal;

import com.idvp.platform.collector.LogDataCollector;
import com.idvp.platform.model.LogData;

import java.util.Optional;

public class JournalRecordCollector<T> implements LogDataCollector {

	private LogDataCollector logDataCollector;

	private JournalRecordTransformer<T> journalRecordTransformer;

	public JournalRecordCollector(JournalRecordTransformer<T> journalRecordTransformer) {
		this.journalRecordTransformer = journalRecordTransformer;
	}

	public Optional<T> getRecord() {
		final LogData[] logData = logDataCollector.getLogData();
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
