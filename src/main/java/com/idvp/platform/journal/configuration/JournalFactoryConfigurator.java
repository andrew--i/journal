package com.idvp.platform.journal.configuration;

import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.Loader;
import com.idvp.platform.journal.JournalException;
import com.idvp.platform.journal.JournalFactory;
import com.idvp.platform.journal.configuration.joran.JoranConfiguration;

import java.net.URL;

public class JournalFactoryConfigurator {

  public static final String AUTOCONFIG_FILE = "idvp.platform.journal.xml";
  public static final String AUTOCONFIG_FILE_PROPERTY = "idvp.platform.journal.config.file";


  public void autoConfig(JournalFactory journalFactory, ClassLoader classLoader) throws JournalException {

    String autoConfigFileByProperty = System.getProperty(AUTOCONFIG_FILE_PROPERTY);
    URL url;

    if (autoConfigFileByProperty != null) {
      url = Loader.getResource(autoConfigFileByProperty, classLoader);
    } else {
      url = Loader.getResource(AUTOCONFIG_FILE, classLoader);
    }
    if (url != null) {
      configureByResource(journalFactory, url);
    } else {
      String errMsg;
      if (autoConfigFileByProperty != null) {
        errMsg = "Failed to find configuration file [" + autoConfigFileByProperty + "].";
      } else {
        errMsg = "Failed to find logback-audit configuration files  [" + AUTOCONFIG_FILE + "].";
      }
      throw new JournalException(errMsg);
    }
  }

  private void configureByResource(JournalFactory context, URL url) throws JournalException {
    JoranConfiguration configurator = new JoranConfiguration();
    configurator.setContext(context);

    try {
      configurator.doConfigure(url);
    } catch (JoranException e) {
      throw new JournalException("Configuration failure in " + url, e);
    }
  }

  public void config(JournalFactory journalFactory, String configPath, ClassLoader tcl) throws JournalException {
    URL url = tcl.getResource(configPath);
    configureByResource(journalFactory, url);
  }
}
