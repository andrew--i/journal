package com.idvp.platform.journal.configuration.joran;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;
import com.idvp.platform.journal.Journal;
import com.idvp.platform.journal.appender.JournalAppender;
import org.xml.sax.Attributes;

public class JournalAppenderAction extends Action {

  private JournalAppender journalAppender;
  private boolean inError = false;

  @Override
  public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
    String className = attributes.getValue(CLASS_ATTRIBUTE);

    // We are just beginning, reset variables
    journalAppender = null;
    inError = false;

    try {
      addInfo("About to instantiate appender of type [" + className + "]");
      journalAppender = JournalAppenderFactory.create(className, context, ic, attributes);

      String appenderName = ic.subst(attributes.getValue(NAME_ATTRIBUTE));

      if (OptionHelper.isEmpty(appenderName)) {
        addWarn(
            "No appender name given for appender of type " + className + "].");
      } else {
        journalAppender.setName(appenderName);
        addInfo("Naming appender as [" + appenderName + "]");
      }

      //getLogger().debug("Pushing appender on to the object stack.");
      ic.pushObject(journalAppender);
    } catch (Exception oops) {
      inError = true;
      addError(
          "Could not create an Appender of type [" + className + "].", oops);
      throw new ActionException(oops);
    }
  }

  @Override
  public void end(InterpretationContext ic, String name) throws ActionException {
    if (inError) {
      return;
    }
    Object o = ic.peekObject();


    if (o != journalAppender) {
      addWarn(
          "The object at the of the stack is not the appender named ["
              + journalAppender.getName() + "] pushed earlier.");
    } else {
      addInfo(
          "Popping appender named [" + journalAppender.getName()
              + "] from the object stack");
      ic.popObject();
    }
    addInfo(
        "Setting journal's appender to appender named [" + journalAppender.getName()
            + "]");

    if (journalAppender instanceof LifeCycle) {
      journalAppender.start();
    }

    Journal journal = (Journal) ic.peekObject();
    journal.setJournalRecordAppender(journalAppender);
  }
}
