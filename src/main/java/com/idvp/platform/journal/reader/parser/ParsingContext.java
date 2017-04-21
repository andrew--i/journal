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
package com.idvp.platform.journal.reader.parser;

import lombok.Data;

import java.text.DateFormat;
import java.util.HashMap;

@Data
public class ParsingContext {

    private StringBuilder unmatchedLog;
    private long lastParsed = 0;
    private int generatedId = 0;
    private volatile boolean parsingInProgress = true;
    private String name;
    private String logSource;
    private HashMap<String, Object> customContextProperties;
    private DateFormat dateFormat;

    public ParsingContext() {
        this("?");
    }

    public ParsingContext(String name) {
        this(name, null);
    }

    public ParsingContext(String name, String logSource) {
        this.name = name;
        this.logSource = logSource;
        unmatchedLog = new StringBuilder();
        customContextProperties = new HashMap<>();
    }

    public int getGeneratedIdAndIncrease() {
        return generatedId++;
    }

}
