package com.idvp.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Journal {

  /**
   * TODO use log4j journal appender
   */
  private Logger logger = LoggerFactory.getLogger(Journal.class.getName());

  public void write(Object record) {
    logger.info(null, record);
  }

  public void write(String key, Object record) {
    logger.info(key, record);
  }


}
