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
package com.idvp.platform.journal.reader.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

@Data
public class LogData implements Serializable {

  private static final long serialVersionUID = -2896759475612130817L;
  private LocalDateTime date = LocalDateTime.now();
  private Level level = Level.INFO;
  private String messageId = "";
  private String clazz = "";
  private String method = "";
  private String file = "";
  private String line = "";
  private String ndc = "";
  private String thread = "";
  private String loggerName = "";
  private String message = "";
  private int id;
  private Map<String, String> properties;
  private String logSource;

  @Override
  public String toString() {
    final int maxLen = 10;
    String builder = "LogData [date=" + date + ", level=" + level + ", messageId=" + messageId + ", clazz=" + clazz +
        ", method=" + method + ", file=" + file + ", line=" + line + ", ndc=" + ndc + ", thread=" +
        thread + ", loggerName=" + loggerName + ", message=" + message + ", id=" + id + ", properties=" +
        (properties != null ? toString(properties.entrySet(), maxLen) : null) + ", logSource=" + logSource + "]";
    return builder;
  }

  private String toString(Collection<?> collection, int maxLen) {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    int i = 0;
    for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
      if (i > 0)
        builder.append(", ");
      builder.append(iterator.next());
    }
    builder.append("]");
    return builder.toString();
  }

}
