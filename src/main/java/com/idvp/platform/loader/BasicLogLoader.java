package com.idvp.platform.loader;

import com.idvp.platform.importer.LogImporter;
import com.idvp.platform.loading.LoadStatistic;
import com.idvp.platform.loading.LoadingDetails;
import com.idvp.platform.loading.LogLoadingSession;
import com.idvp.platform.loading.Source;
import com.idvp.platform.collector.LogDataCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class BasicLogLoader implements LogLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasicLogLoader.class);
  public static final int DEFAULT_SLEEP_TIME = 3000;


  private final Map<LogLoadingSession, LoadingRunnable> lrMap = new HashMap<>();
  private final Map<LogDataCollector, List<LogLoadingSession>> ldCollectorToSession = new HashMap<>();


  @Override
  public LogLoadingSession startLoading(Source source, LogImporter logImporter, LogDataCollector logDataCollector) {
    return startLoading(source, logImporter, logDataCollector, DEFAULT_SLEEP_TIME, Optional.empty());
  }

  @Override
  public LogLoadingSession startLoading(Source source, LogImporter logImporter, LogDataCollector logDataCollector, long sleepTime, Optional<Long> bufferingTime) {


    final LoadingRunnable loadingRunnable = new LoadingRunnable(source, logImporter, logDataCollector, sleepTime, bufferingTime);
    final Thread thread = new Thread(loadingRunnable);
    thread.setDaemon(true);
    thread.start();
    String id = UUID.randomUUID().toString(); //TODO replace this with something meaningful
    LogLoadingSession session = new LogLoadingSession(id, source);
    lrMap.put(session, loadingRunnable);
    final List<LogLoadingSession> sessionsForCollector = ldCollectorToSession.getOrDefault(logDataCollector, new ArrayList<>());
    sessionsForCollector.add(session);
    ldCollectorToSession.put(logDataCollector,sessionsForCollector);
    LOGGER.info("Started {} ", id);
    return session;
  }

  @Override
  public void pause(LogLoadingSession logLoadingSession) {
    if (lrMap.containsKey(logLoadingSession)) {
      LOGGER.info("Pausing {} ", logLoadingSession);
      final LoadingRunnable loadingRunnable = lrMap.get(logLoadingSession);
      loadingRunnable.pause();
    } else {
      LOGGER.info("Pausing {} will not work, don't have this loading session",logLoadingSession );
    }
  }

  @Override
  public void resume(LogLoadingSession logLoadingSession) {
    if (lrMap.containsKey(logLoadingSession)) {
      LOGGER.info("Resuming {} ", logLoadingSession.getId());
      final LoadingRunnable loadingRunnable = lrMap.get(logLoadingSession);
      loadingRunnable.resume();
    } else {
      final String map = lrMap.entrySet().stream().map(es -> es.getKey() + "/" + es.getValue()).collect(Collectors.joining());
      LOGGER.info("Resuming {} will not work, don't have this loading session, all:\n{}", logLoadingSession.getId(),map );}
  }

  @Override
  public void stop(LogLoadingSession logLoadingSession) {
    if (lrMap.containsKey(logLoadingSession)) {
      LOGGER.info("Stopping {} ", logLoadingSession);
      final LoadingRunnable loadingRunnable = lrMap.get(logLoadingSession);
      loadingRunnable.stop();
    }
  }

  @Override
  public void close(LogLoadingSession logLoadingSession) {
    lrMap.computeIfPresent(logLoadingSession, (id, loadingRunnable) -> {
      LOGGER.info("Closing {} ", id);
      loadingRunnable.stop();

      return loadingRunnable;
    });
  }

  @Override
  public void close(LogDataCollector logDataCollector) {
    LOGGER.info("Closing {} should stop runnable!", logDataCollector);
    ldCollectorToSession.getOrDefault(logDataCollector, new ArrayList<>())
      .stream()
      .forEach(this::stop);
    logDataCollector.clear();

  }


  @Override
  public LoadStatistic getLoadStatistic(LogLoadingSession logLoadingSession) {
    final LoadingRunnable loadingRunnable = lrMap.get(logLoadingSession);
    return loadingRunnable.getLoadStatistic();
  }

  @Override
  public LoadingDetails getLoadingDetails(LogDataCollector logDataCollector) {
    List<LogLoadingSession> logLoadingSessions = ldCollectorToSession.getOrDefault(logDataCollector, new ArrayList<>());
    return new LoadingDetails(logLoadingSessions, logDataCollector);
  }

  @Override
  public void shutdown() {
    System.out.println("Shutting down");
    lrMap.values().stream().forEach(LoadingRunnable::stop);
    lrMap.clear();
  }
}
