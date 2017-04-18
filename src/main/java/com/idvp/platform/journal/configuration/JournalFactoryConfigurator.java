package com.idvp.platform.journal.configuration;

import ch.qos.logback.core.joran.spi.JoranException;
import com.idvp.platform.journal.JournalException;
import com.idvp.platform.journal.JournalFactory;
import com.idvp.platform.journal.configuration.joran.JoranConfiguration;
import org.xml.sax.InputSource;

import java.io.Reader;

public class JournalFactoryConfigurator {


  public void configureByResourceStream(JournalFactory context, Reader reader) throws JournalException {
    JoranConfiguration configurator = new JoranConfiguration();
    configurator.setContext(context);
    try {
      configurator.doConfigure(new InputSource(reader));
    } catch (JoranException e) {
      throw new JournalException("Configuration failure ", e);
    }
  }

}
