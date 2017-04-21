package com.idvp.platform.journal.reader.parser.simple;

import com.idvp.platform.journal.reader.model.LogData;
import com.idvp.platform.journal.reader.model.LogDataBuilder;
import com.idvp.platform.journal.reader.parser.LogParser;
import com.idvp.platform.journal.reader.parser.ParserDescription;
import com.idvp.platform.journal.reader.parser.ParsingContext;

import java.text.ParseException;
import java.util.Properties;

public class LineLogParser implements LogParser {

    String charset = "UTF-8";
    String displayName = "LineLogParser";
    String logSource;

    @Override
    public void init(Properties properties) {

    }

    @Override
    public void initParsingContext(ParsingContext parsingContext) {

        logSource = parsingContext.getLogSource();
    }

    @Override
    public LogData parse(String line, ParsingContext parsingContext) throws ParseException {
        return new LogDataBuilder()
                .withMessage(line)
                .withThread(Thread.currentThread().getName())
                .build();
    }

    @Override
    public ParserDescription getParserDescription() {
        return new ParserDescription(displayName, charset, logSource);
    }

}
