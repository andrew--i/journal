package com.idvp.platform.journal.reader.loader;


import com.idvp.platform.journal.reader.collector.LogDataCollector;
import com.idvp.platform.journal.reader.importer.LogImporter;
import com.idvp.platform.journal.reader.loading.LoadStatistic;
import com.idvp.platform.journal.reader.loading.LoadingDetails;
import com.idvp.platform.journal.reader.loading.LogLoadingSession;
import com.idvp.platform.journal.reader.loading.VfsSource;

public interface LogLoader {

  LogLoadingSession startLoading(VfsSource source, LogImporter logImporter, LogDataCollector logDataCollector);

  LogLoadingSession startLoading(VfsSource source, LogImporter logImporter, LogDataCollector logDataCollector, long sleepTime);

  void pause(LogLoadingSession logLoadingSession);

  void resume(LogLoadingSession logLoadingSession);

  void stop(LogLoadingSession logLoadingSession);

  void close(LogLoadingSession logDataCollector);

  void close(LogDataCollector logDataCollector);

  LoadStatistic getLoadStatistic(LogLoadingSession logLoadingSession);

  LoadingDetails getLoadingDetails(LogDataCollector logDataCollector);

  void shutdown();
}
