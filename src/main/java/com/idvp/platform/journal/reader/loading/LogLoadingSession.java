package com.idvp.platform.journal.reader.loading;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class LogLoadingSession {

  @Getter
  private String id;
  @Getter
  private VfsSource source;


  @Override
  public String toString() {
    return "LogLoadingSession{" +
        "id='" + id + '\'' +
        ", source=" + source +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LogLoadingSession session = (LogLoadingSession) o;

    return id != null ? id.equals(session.id) : session.id == null;

  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}