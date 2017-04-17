package com.idvp.platform.journal.configuration.joran;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.StatusPrinter;
import com.idvp.platform.journal.JournalFactory;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;

public class JournalFactoryAction extends Action {

  private static final String INTERNAL_DEBUG_ATTR = "debug";
  private boolean debugMode = false;

  @Override
  public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {

    if (StringUtils.isEmpty(attributes.getValue(INTERNAL_DEBUG_ATTR))) {
      addInfo("Ignoring " + INTERNAL_DEBUG_ATTR + " attribute.");
    } else {
      debugMode = true;
    }

  }

  @Override
  public void end(InterpretationContext ic, String name) throws ActionException {
    if (debugMode) {
      addInfo("End of configuration.");
      StatusPrinter.print(context);
    }
  }
}
