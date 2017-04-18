package com.idvp.platform.journal.configuration.joran;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.DynamicClassLoadingException;
import ch.qos.logback.core.util.IncompatibleClassException;
import ch.qos.logback.core.util.OptionHelper;
import com.idvp.platform.journal.Journal;
import com.idvp.platform.journal.reader.SourceFactory;
import com.idvp.platform.journal.reader.JournalRecordsReader;
import com.idvp.platform.journal.reader.collector.LogDataCollector;
import com.idvp.platform.journal.reader.collector.TopLogDataCollector;
import com.idvp.platform.journal.reader.loading.VfsSource;
import org.apache.commons.vfs2.FileSystemException;
import org.xml.sax.Attributes;

public class JournalReaderFactory {


  public static JournalRecordsReader create(String collectorClassName, String sourceValue, Context context, InterpretationContext ic, Attributes attributes) throws DynamicClassLoadingException, IncompatibleClassException, FileSystemException {
    VfsSource source = SourceFactory.create(sourceValue);
    Journal journal = (Journal) ic.peekObject();
    if (collectorClassName.equalsIgnoreCase(TopLogDataCollector.class.getName()))
      return createTopLogDataCollector(source, journal, ic, attributes);
    else {
      LogDataCollector collector = (LogDataCollector) OptionHelper.instantiateByClassName(collectorClassName, LogDataCollector.class, context);
      return new JournalRecordsReader(source, journal.getTClass(), collector);
    }

  }

  private static JournalRecordsReader createTopLogDataCollector(VfsSource source, Journal journal, InterpretationContext ic, Attributes attributes) {
    String topSize = ic.subst(attributes.getValue("topSize"));
    TopLogDataCollector topLogDataCollector = new TopLogDataCollector(Integer.parseInt(topSize));
    return new JournalRecordsReader(source, journal.getTClass(), topLogDataCollector);
  }
}
