package com.idvp.platform.loading;

import com.idvp.platform.collector.LogDataCollector;
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
