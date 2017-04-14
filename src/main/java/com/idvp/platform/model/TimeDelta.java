package com.idvp.platform.model;

import java.time.LocalTime;

public class TimeDelta {
  private final LocalTime timestamp;

  public TimeDelta(LocalTime timestamp) {
    this.timestamp = timestamp;
  }

  public LocalTime getTimestamp() {
    return timestamp;
  }
}
