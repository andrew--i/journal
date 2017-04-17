package com.idvp.platform.journal.configuration.joran;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import com.idvp.platform.journal.Journal;
import com.idvp.platform.journal.reader.JournalRecordsReader;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;

public class JournalReaderAction extends Action {

  private JournalRecordsReader journalRecordsReader;
  private boolean inError = false;
  private final String COLLECTOR_ATTR = "collector";
  private static final String SOURCE_ATTR = "source";

  @Override
  public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {

    String collectorClassName = ic.subst(attributes.getValue(COLLECTOR_ATTR));

    // We are just beginning, reset variables
    journalRecordsReader = null;
    inError = false;


    String sourceValue = ic.subst(attributes.getValue(SOURCE_ATTR));
    if (StringUtils.isEmpty(sourceValue)) {
      addError("Journal attribute \"source\" is mandatory");
    }

    try {
      addInfo("About to instantiate reader of type [" + collectorClassName + "]");
      journalRecordsReader = JournalReaderFactory.create(collectorClassName, sourceValue, context, ic, attributes);

      String readerName = ic.subst(attributes.getValue(NAME_ATTRIBUTE));

      if (OptionHelper.isEmpty(readerName)) {
        addWarn("No reader name given for appender of type " + collectorClassName + "].");
      } else {
        journalRecordsReader.setName(readerName);
        addInfo("Naming reader as [" + readerName + "]");
      }

      ic.pushObject(journalRecordsReader);
    } catch (Exception oops) {
      inError = true;
      addError("Could not create an Reader of type [" + collectorClassName + "].", oops);
      throw new ActionException(oops);
    }
  }

  @Override
  public void end(InterpretationContext ic, String name) throws ActionException {
    if (inError) {
      return;
    }
    Object o = ic.peekObject();


    if (o != journalRecordsReader) {
      addWarn(
          "The object at the of the stack is not the appender named ["
              + journalRecordsReader.getName() + "] pushed earlier.");
    } else {
      addInfo(
          "Popping appender named [" + journalRecordsReader.getName()
              + "] from the object stack");
      ic.popObject();
    }
    addInfo(
        "Setting journal's appender to appender named [" + journalRecordsReader.getName()
            + "]");

    Journal journal = (Journal) ic.peekObject();
    journal.setJournalRecordsReader(journalRecordsReader);
  }
}
