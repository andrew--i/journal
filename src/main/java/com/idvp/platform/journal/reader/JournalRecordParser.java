package com.idvp.platform.journal.reader;

import com.idvp.platform.journal.JournalRecordTransformer;
import com.idvp.platform.journal.reader.model.LogData;
import com.idvp.platform.journal.reader.parser.LogParser;
import com.idvp.platform.journal.reader.parser.ParserDescription;
import com.idvp.platform.journal.reader.parser.ParsingContext;

import java.text.ParseException;
import java.util.Properties;

public class JournalRecordParser<T> implements LogParser {

  private String logSource;

  private final static String charset = "UTF-8";
  private final static String displayName = "Journal Record Log Parser";
  private JournalRecordTransformer<T> transformer;


  public JournalRecordParser(JournalRecordTransformer<T> transformer) {
    this.transformer = transformer;
  }

  @Override
  public void init(Properties properties) {

  }

  @Override
  public void initParsingContext(ParsingContext parsingContext) {
    this.logSource = parsingContext.getLogSource();
  }

  @Override
  public LogData parse(String line, ParsingContext parsingContext) throws ParseException {
    return transformer.toLogData(line, parsingContext);
  }

  @Override
  public ParserDescription getParserDescription() {
    return new ParserDescription(displayName, charset, logSource);
  }
}
