/*******************************************************************************
 * Copyright 2011 Krzysztof Otrebski
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.idvp.platform.journal.reader.importer;


import com.idvp.platform.journal.reader.collector.LogDataCollector;
import com.idvp.platform.journal.reader.parser.ParsingContext;

import java.io.InputStream;
import java.util.Properties;

public interface LogImporter {

    void init(Properties properties);

    /**
     * Initialize parsing context specific resources, which are not thread safe (i.e. DateFormat)
     *
     * @param parsingContext
     */
    void initParsingContext(ParsingContext parsingContext);

    void importLogs(InputStream in, LogDataCollector dataCollector, ParsingContext parsingContext);
}
