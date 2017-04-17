package com.idvp.platform.journal.configuration.joran;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import com.idvp.platform.journal.Journal;
import com.idvp.platform.journal.JournalFactory;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;

public class JournalAction extends Action {



  @Override
  public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {

    final String key = ic.subst(attributes.getValue(KEY_ATTRIBUTE));
    if (StringUtils.isEmpty(key))
      addError("Journal attribute \"key\" is mandatory");
    final String sClass = ic.subst(attributes.getValue(CLASS_ATTRIBUTE));
    if (StringUtils.isEmpty(sClass))
      addError("Journal attribute \"class\" is mandatory");



    if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(sClass))
      try {

        ic.pushObject(new Journal(key, Class.forName(sClass)));
      } catch (ClassNotFoundException e) {
        addError("Journal record class not found", e);
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
