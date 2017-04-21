package com.idvp.platform.journal.reader.loading;

import lombok.Getter;

import java.time.LocalDate;

public class LoadStatistic {

    @Getter
    private VfsSource source;
    @Getter
    private long position;
    @Getter
    private long total;
    @Getter
    private LocalDate date;

    public LoadStatistic(VfsSource source, long position, long total) {
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
