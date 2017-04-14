package com.idvp.platform.journal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idvp.platform.model.LogData;
import com.idvp.platform.parser.LogParser;
import com.idvp.platform.parser.ParserDescription;
import com.idvp.platform.parser.ParsingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;
import java.util.Properties;

public class JournalRecordTransformer<T> implements LogParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(JournalRecordTransformer.class);

	private final Class<T> tClass;
	private ObjectMapper objectMapper;
	private Properties properties;
	private ParsingContext parsingContext;
	private String logSource;

	private final static String charset = "UTF-8";
	private final static String displayName = "Journal Record Log Parser";

	public JournalRecordTransformer(Class<T> tClass) {
		this.objectMapper = new ObjectMapper();
		this.tClass = tClass;
	}

	public Optional<String> toString(T record) {
		try {
			return Optional.of(objectMapper.writeValueAsString(record));
		} catch (JsonProcessingException e) {
			LOGGER.warn("Could not serialize journal record", e);
			return Optional.empty();
		}
	}

	public Optional<T> fromString(String value) {
		try {
			return Optional.of(objectMapper.readValue(value, tClass));
		} catch (IOException e) {
			LOGGER.warn("Could not deserialize journal record", e);
			return Optional.empty();
		}
	}

	@Override
	public void init(Properties properties) {
		this.properties = properties;
	}

	@Override
	public void initParsingContext(ParsingContext parsingContext) {
		this.parsingContext = parsingContext;
		this.logSource = parsingContext.getLogSource();
	}

	@Override
	public LogData parse(String line, ParsingContext parsingContext) throws ParseException {
		return null;
	}

	@Override
	public ParserDescription getParserDescription() {
		return new ParserDescription(displayName, charset, logSource);
	}
}
