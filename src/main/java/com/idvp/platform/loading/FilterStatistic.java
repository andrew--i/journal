package com.idvp.platform.loading;

import lombok.Getter;

import java.time.LocalDate;


public class FilterStatistic {

    @Getter
    private LogLoadingSession logLoadingSession;
    @Getter
    private LocalDate date;
    @Getter
    private long processed = 0;
    @Getter
    private long passed = 0;
    @Getter
    private long rejected = 0;

    public FilterStatistic(LogLoadingSession logLoadingSession, long processed, long passed, long rejected) {
        this.logLoadingSession = logLoadingSession;
        this.processed = processed;
        this.passed = passed;
        this.rejected = rejected;
        this.date = LocalDate.now();
    }

}
