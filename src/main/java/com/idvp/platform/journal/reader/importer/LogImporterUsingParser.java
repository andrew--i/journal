/*
 * Copyright 2012 Krzysztof Otrebski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.idvp.platform.journal.reader.importer;

import com.idvp.platform.journal.reader.collector.LogDataCollector;
import com.idvp.platform.journal.reader.model.LogData;
import com.idvp.platform.journal.reader.parser.LogParser;
import com.idvp.platform.journal.reader.parser.MultiLineLogParser;
import com.idvp.platform.journal.reader.parser.ParsingContext;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Properties;

public class LogImporterUsingParser implements LogImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogImporterUsingParser.class.getName());
    private LogParser parser = null;

    public LogImporterUsingParser(LogParser parser) {
        super();
        this.parser = parser;
    }

    @Override
    public void init(Properties properties) {
        parser.init(properties);
    }

    @Override
    public void importLogs(InputStream in, final LogDataCollector dataCollector, ParsingContext parsingContext) {
        LOGGER.trace("Log import started ");
        String line;
        LogData logData;
        String charset = parser.getParserDescription().getCharset();

        BufferedReader logReader;
        if (charset == null) {
            logReader = new BufferedReader(new InputStreamReader(in));
        } else {
            try {
                logReader = new BufferedReader(new InputStreamReader(in, charset));
            } catch (UnsupportedEncodingException e1) {
                LOGGER.error(String.format("Required charset [%s] is not supported: %s", charset, e1.getMessage()));
                LOGGER.info(String.format("Using default charset: %s", Charset.defaultCharset().displayName()));
                logReader = new BufferedReader(new InputStreamReader(in));
            }

        }
        while (true) {
            synchronized (parsingContext) {
                if (!parsingContext.isParsingInProgress()) {
                    break;
                }
            }
            try {
                line = logReader.readLine();
                if (line == null) {
                    break;
                }

                if (parser instanceof MultiLineLogParser) {
                    synchronized (parsingContext) {
                        logData = parser.parse(line, parsingContext);
                    }
                } else {
                    logData = parser.parse(line, parsingContext);
                }

                if (logData != null) {
                    logData.setId(parsingContext.getGeneratedIdAndIncrease());
                    logData.setLogSource(parsingContext.getLogSource());
                    dataCollector.add(logData);
                    parsingContext.setLastParsed(System.currentTimeMillis());
                }

            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error(String.format("IOException during log import (file %s): %s", parsingContext.getLogSource(), e.getMessage()));
                break;
            } catch (ParseException e) {
                LOGGER.error(String.format("ParseException during log import (file %s): %s", parsingContext.getLogSource(), e.getMessage()));
                e.printStackTrace();
                break;
            }
        }

        parseBuffer(dataCollector, parsingContext);

        IOUtils.closeQuietly(logReader);
        LOGGER.trace("Log import finished!");
    }

    private void parseBuffer(LogDataCollector dataCollector, ParsingContext parsingContext) {
        try {
            if (parser instanceof MultiLineLogParser) {
                MultiLineLogParser multiLineLogParser = (MultiLineLogParser) parser;
                LogData logData = multiLineLogParser.parseBuffer(parsingContext);
                if (logData != null) {
                    logData.setId(parsingContext.getGeneratedIdAndIncrease());
                    logData.setLogSource(parsingContext.getLogSource());
                    synchronized (parsingContext) {
                        dataCollector.add(logData);
                    }
                    parsingContext.setLastParsed(System.currentTimeMillis());
                }
            }
        } catch (Exception e) {
            LOGGER.info("Cannot parser rest of buffer, probably stopped importing");
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LogImporterUsingParser && parser.equals(((LogImporterUsingParser) obj).getParser());
    }

    @Override
    public int hashCode() {
        return parser.hashCode();
    }

    public LogParser getParser() {
        return parser;
    }

    @Override
    public void initParsingContext(ParsingContext parsingContext) {
        parser.initParsingContext(parsingContext);
    }

    @Override
    public String toString() {
        String s = super.toString();
        if (parser != null && parser.getParserDescription() != null) {
            s = parser.getParserDescription().getDisplayName();
        }
        return s;
    }
}
