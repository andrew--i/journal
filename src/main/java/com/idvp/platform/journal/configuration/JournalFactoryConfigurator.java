package com.idvp.platform.journal.configuration;

import ch.qos.logback.core.joran.spi.JoranException;
import com.idvp.platform.journal.JournalException;
import com.idvp.platform.journal.JournalFactory;
import com.idvp.platform.journal.configuration.joran.JoranConfiguration;

import java.io.InputStream;

public class JournalFactoryConfigurator {


  public void configureByResourceStream(JournalFactory context, InputStream configStream) throws JournalException {
    JoranConfiguration configurator = new JoranConfiguration();
    configurator.setContext(context);
    try {
      configurator.doConfigure(configStream);
    } catch (JoranException e) {
      throw new JournalException("Configuration failure in " + configStream, e);
    }
  }

}
