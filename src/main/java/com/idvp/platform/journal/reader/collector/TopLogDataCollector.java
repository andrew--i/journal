package com.idvp.platform.journal.reader.collector;

import com.idvp.platform.journal.reader.model.LogData;

import java.util.Comparator;
import java.util.stream.Collectors;

public class TopLogDataCollector extends ProxyLogDataCollector {


    private static final Comparator<LogData> dateComparator = (o1, o2) -> {
        if (o1.getDate() == null || o2.getDate() == null)
            throw new IllegalArgumentException("Field date is mandatory");
        return -o1.getDate().compareTo(o2.getDate());
    };
    private int topSize = 5;

    public TopLogDataCollector(int topSize) {
        this.topSize = topSize;
    }

    public void setTopSize(int topSize) {
        if (topSize < 1)
            throw new IllegalArgumentException("Field topSize must > 0");
        this.topSize = topSize;
    }

    @Override
    public void add(LogData... logDatas) {
        super.add(logDatas);
        list.sort(dateComparator);
        list = list.stream().limit(topSize).collect(Collectors.toList());
    }
}
