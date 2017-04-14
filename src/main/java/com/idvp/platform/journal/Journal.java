package com.idvp.platform.journal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Journal<T> {

	/**
	 * TODO use log4j journal appender
	 */
	private Logger journalWriter = LoggerFactory.getLogger(Journal.class.getName());
	/**
	 * Ключ журнала, уникальный в рамках всех журналов
	 */
	private String key;

	/**
	 * Собирает записи журнала
	 */
	private JournalRecordCollector<T> journalRecordCollector;

	/**
	 * Загрузчик записей журнала
	 */
	private JournalRecordsLoader<T> journalRecordsLoader;

	/**
	 * Преобразует записи журнала в строки/объекты
	 */
	private JournalRecordTransformer<T> journalRecordTransformer;


	public Journal(String key, Class<T> tClass) {
		this.key = key;
		this.journalRecordTransformer = new JournalRecordTransformer<>(tClass);
		this.journalRecordCollector = new JournalRecordCollector<>(this.journalRecordTransformer);
		this.journalRecordsLoader = new JournalRecordsLoader<>(key, this.journalRecordCollector, this.journalRecordTransformer);
	}

	public void open() {
		this.journalRecordsLoader.open();

	}

	public void close() {
		journalRecordsLoader.close();
	}


	public void write(T record) {
		final Optional<String> writableRecord = journalRecordTransformer.toString(record);
		writableRecord.ifPresent(r -> journalWriter.info(r, key));
	}


	public Optional<T> read() {
		return journalRecordCollector.getRecord();
	}
}
