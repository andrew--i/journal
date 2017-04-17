package com.idvp.platform.journal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
}
