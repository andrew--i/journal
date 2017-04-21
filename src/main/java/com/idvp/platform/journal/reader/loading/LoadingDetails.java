package com.idvp.platform.journal.reader.loading;

import com.idvp.platform.journal.reader.collector.LogDataCollector;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class LoadingDetails {

    @Getter
    private List<LogLoadingSession> logLoadingSessions;
    @Getter
    private LogDataCollector logDataCollector;
}
