package com.idvp.platform.journal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idvp.platform.journal.reader.model.LogData;
import com.idvp.platform.journal.reader.model.LogDataBuilder;
import com.idvp.platform.journal.reader.parser.ParsingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public class JournalRecordTransformer<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(JournalRecordTransformer.class);

  private final Class<T> tClass;
  private ObjectMapper objectMapper;

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

  public LogData toLogData(String line, ParsingContext parsingContext) {
    try {
      Map<String, Object> map = objectMapper.readValue(line, Map.class);
      return new LogDataBuilder()
          .withDate(LocalDateTime.parse((CharSequence) map.get("@timestamp"), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
          .withFile(parsingContext.getLogSource())
          .withLevel((String) map.get("level"))
          .withLoggerName((String) map.get("logger_name"))
          .withMessage((String) map.get("message"))
          .build();
    } catch (IOException e) {
      LOGGER.warn("Could not parse string %s", line);
      return null;
    }

  }
}
