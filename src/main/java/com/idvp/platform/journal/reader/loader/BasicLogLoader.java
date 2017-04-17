package com.idvp.platform.journal.reader.loader;

import com.idvp.platform.journal.reader.collector.LogDataCollector;
import com.idvp.platform.journal.reader.importer.LogImporter;
import com.idvp.platform.journal.reader.loading.LoadStatistic;
import com.idvp.platform.journal.reader.loading.LoadingDetails;
import com.idvp.platform.journal.reader.loading.LogLoadingSession;
import com.idvp.platform.journal.reader.loading.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BasicLogLoader implements LogLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasicLogLoader.class);
  public static final int DEFAULT_SLEEP_TIME = 3000;


  private final Map<LogLoadingSession, LoadingRunnable> lrMap = new ConcurrentHashMap<>();
  private final Map<LogDataCollector, List<LogLoadingSession>> ldCollectorToSession = new ConcurrentHashMap<>();


  @Override
  public LogLoadingSession startLoading(Source source, LogImporter logImporter, LogDataCollector logDataCollector) {
    return startLoading(source, logImporter, logDataCollector, DEFAULT_SLEEP_TIME);
  }

  @Override
  public LogLoadingSession startLoading(Source source, LogImporter logImporter, LogDataCollector logDataCollector, long sleepTime) {


    final LoadingRunnable loadingRunnable = new LoadingRunnable(source, logImporter, logDataCollector, sleepTime);
    final Thread thread = new Thread(loadingRunnable);
    thread.setDaemon(true);
    thread.start();
    String id = UUID.randomUUID().toString(); //TODO replace this with something meaningful
    LogLoadingSession session = new LogLoadingSession(id, source);
    lrMap.put(session, loadingRunnable);
    final List<LogLoadingSession> sessionsForCollector = ldCollectorToSession.getOrDefault(logDataCollector, new ArrayList<>());
    sessionsForCollector.add(session);
    ldCollectorToSession.put(logDataCollector, sessionsForCollector);
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
      LOGGER.info("Pausing {} will not work, don't have this loading session", logLoadingSession);
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
      LOGGER.info("Resuming {} will not work, don't have this loading session, all:\n{}", logLoadingSession.getId(), map);
    }
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
    LOGGER.info("Shutting down");
    lrMap.values().stream().forEach(LoadingRunnable::stop);
    lrMap.clear();
  }
}
