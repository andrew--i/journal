package com.idvp.platform.journal.configuration.joran;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.StatusPrinter;
import com.idvp.platform.journal.Journal;
import com.idvp.platform.journal.JournalFactory;
import com.idvp.platform.journal.SourceFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileSystemException;
import org.xml.sax.Attributes;

public class JournalAction extends Action {
  private static final String SOURCE_ATTR = "source";


  @Override
  public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {

    final String key = attributes.getValue(KEY_ATTRIBUTE);
    if (StringUtils.isEmpty(key))
      addError("Journal attribute \"key\" is mandatory");
    final String sClass = attributes.getValue(CLASS_ATTRIBUTE);
    if (StringUtils.isEmpty(sClass))
      addError("Journal attribute \"class\" is mandatory");


    String sourceValue = attributes.getValue(SOURCE_ATTR);
    if (StringUtils.isEmpty(sourceValue)) {
      addError("Journal attribute \"source\" is mandatory");
    }


    if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(sClass) && !StringUtils.isEmpty(sourceValue))
      try {

        ic.pushObject(new Journal(key, Class.forName(sClass), SourceFactory.create(sourceValue)));
      } catch (ClassNotFoundException e) {
        addError("Journal record class not found", e);
      } catch (FileSystemException e) {
        addError("Journal source error", e);
      }
  }

  @Override
  public void end(InterpretationContext ic, String name) throws ActionException {

    if (!ic.isEmpty()) {
      Journal journal = (Journal) ic.popObject();
      ((JournalFactory) context).addJournal(journal);
    }
  }
}