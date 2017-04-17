package com.idvp.platform.journal.configuration.joran;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.DynamicClassLoadingException;
import ch.qos.logback.core.util.IncompatibleClassException;
import ch.qos.logback.core.util.OptionHelper;
import com.idvp.platform.journal.Journal;
import com.idvp.platform.journal.writer.JournalAppender;
import com.idvp.platform.journal.writer.SLF4JJournalAppender;
import org.xml.sax.Attributes;

public class JournalAppenderFactory {
  public static JournalAppender create(String className, Context context, InterpretationContext ic, Attributes attributes) throws DynamicClassLoadingException, IncompatibleClassException, ClassNotFoundException {
    JournalAppender journalAppender;
    if (className.equalsIgnoreCase(SLF4JJournalAppender.class.getName()))
      journalAppender = createSLF4jAppender(className, attributes, context, ic);
    else
      journalAppender = (JournalAppender) OptionHelper.instantiateByClassName(
          className, JournalAppender.class, context);
    journalAppender.setContext(context);
    return journalAppender;
  }

  private static JournalAppender createSLF4jAppender(String className, Attributes attributes, Context context, InterpretationContext ic) throws ClassNotFoundException, DynamicClassLoadingException, IncompatibleClassException {
    String loggerName = attributes.getValue("logger");
    if (loggerName == null)
      throw new IllegalArgumentException("Param \"logger\" (logger name) is mandatory");
    SLF4JJournalAppender.SLF4jJournalAppenderParameter parameter = new SLF4JJournalAppender.SLF4jJournalAppenderParameter<>();
    parameter.loggerName = loggerName;
    Journal journal = (Journal) ic.peekObject();
    parameter.tClass = journal.getTClass();
    return (JournalAppender) OptionHelper.instantiateByClassNameAndParameter(className, JournalAppender.class, context, SLF4JJournalAppender.SLF4jJournalAppenderParameter.class, parameter);
  }
}
