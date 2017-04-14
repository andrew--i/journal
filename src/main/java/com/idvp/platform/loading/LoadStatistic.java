package com.idvp.platform.loading;

import lombok.Getter;

import java.time.LocalDate;

public class LoadStatistic {

    @Getter
    private Source source;
    @Getter
    private long position;
    @Getter
    private long total;
    @Getter
    private LocalDate date;

    public LoadStatistic(Source source, long position, long total) {
        this.source = source;
        this.position = position;
        this.total = total;
        date = LocalDate.now();
    }

    @Override
    public String toString() {
        return "LoadStatistic{" +
                "source=" + source +
                ", position=" + position +
                ", total=" + total +
                ", date=" + date +
                '}';
    }
}
