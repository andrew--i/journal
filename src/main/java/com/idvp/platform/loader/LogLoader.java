package com.idvp.platform.loader;




import com.idvp.platform.importer.LogImporter;
import com.idvp.platform.loading.LoadStatistic;
import com.idvp.platform.loading.LoadingDetails;
import com.idvp.platform.loading.LogLoadingSession;
import com.idvp.platform.loading.Source;
import com.idvp.platform.collector.LogDataCollector;

import java.util.Optional;

public interface LogLoader {

  LogLoadingSession startLoading(Source source, LogImporter logImporter, LogDataCollector logDataCollector);

  LogLoadingSession startLoading(Source source, LogImporter logImporter, LogDataCollector logDataCollector, long sleepTime, Optional<Long> bufferingTime);

  void pause(LogLoadingSession logLoadingSession);
  void resume(LogLoadingSession logLoadingSession);
  void stop(LogLoadingSession logLoadingSession);
  void close(LogLoadingSession logDataCollector);
  void close(LogDataCollector logDataCollector);
  LoadStatistic getLoadStatistic(LogLoadingSession logLoadingSession);
  LoadingDetails getLoadingDetails(LogDataCollector logDataCollector);
  void shutdown();
}
