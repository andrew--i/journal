package com.idvp.platform.journal.reader.loader;

import com.idvp.platform.journal.reader.collector.LogDataCollector;
import com.idvp.platform.journal.reader.importer.LogImporter;
import com.idvp.platform.journal.reader.io.LoadingInfo;
import com.idvp.platform.journal.reader.io.ObservableInputStreamImpl;
import com.idvp.platform.journal.reader.io.Utils;
import com.idvp.platform.journal.reader.loading.LoadStatistic;
import com.idvp.platform.journal.reader.loading.SocketSource;
import com.idvp.platform.journal.reader.loading.Source;
import com.idvp.platform.journal.reader.loading.VfsSource;
import com.idvp.platform.journal.reader.parser.ParsingContext;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Optional;

public class LoadingRunnable implements Runnable {

  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LoadingRunnable.class);
  private final Source source;
  private LogDataCollector logDataCollector;
  private final long sleepTime;

  private volatile boolean pause = false;
  private volatile boolean stop = false;
  private LogImporter importer;
  private long lastFileSize = 0;
  private Optional<ObservableInputStreamImpl> observableInputStream = Optional.empty();

  private enum SleepAction {Sleep, Break, Import}

  public LoadingRunnable(Source source, LogImporter logImporter, LogDataCollector logDataCollector, long sleepTime) {
    this.source = source;
    this.logDataCollector = logDataCollector;
    this.sleepTime = sleepTime;
    this.importer = logImporter;
  }

  @Override
  public void run() {
    if (source instanceof VfsSource) {
      runWithVfs((VfsSource) source);
    } else if (source instanceof SocketSource) {
      runWithSocket((SocketSource) source);
    } else {
      LOGGER.error("Not support source type: " + source);
    }
  }

  private void runWithSocket(SocketSource source) {
    final ParsingContext parsingContext = new ParsingContext("Socket", "Socket " + source.getSocket().getRemoteSocketAddress());
    importer.initParsingContext(parsingContext);
    try {
      final InputStream inputStream = new BufferedInputStream(source.getSocket().getInputStream());
      final ObservableInputStreamImpl observableInputStream = new ObservableInputStreamImpl(inputStream);

      LOGGER.debug("Starting main loop");
      while (parsingContext.isParsingInProgress()) {
        try {
          SleepAction action;
          synchronized (this) {
            if (stop) {
              action = SleepAction.Break;
            } else if (pause) {
              action = SleepAction.Sleep;
            } else {
              action = SleepAction.Import;
            }
          }
          if (SleepAction.Sleep == action) {
            Thread.sleep(sleepTime);
          } else if (SleepAction.Break == action) {
            break;
          } else {
            importer.importLogs(observableInputStream, logDataCollector, parsingContext);
          }

          Thread.sleep(sleepTime);

        } catch (Exception e) {
          LOGGER.warn("Exception in tailing loop: " + e.getMessage());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    LOGGER.info("Log importing finished");
  }

  private void runWithVfs(VfsSource vfs) {
    try {
      final LoadingInfo loadingInfo = Utils.openFileObject(vfs.getFileObject(), true);
      ParsingContext parsingContext = new ParsingContext(
          loadingInfo.getFileObject().getName().getFriendlyURI(),
          loadingInfo.getFileObject().getName().getBaseName());

      importer.initParsingContext(parsingContext);
      Utils.reloadFileObject(loadingInfo, vfs.getPosition());
      try {
        loadingInfo.setLastFileSize(loadingInfo.getFileObject().getContent().getSize());
      } catch (FileSystemException e1) {
        LOGGER.warn("Can't initialize start position for tailing. Can duplicate some values for small files");
      }

      while (parsingContext.isParsingInProgress()) {
        try {
          SleepAction action;
          observableInputStream = Optional.of(loadingInfo.getObservableInputStreamImpl());
          synchronized (this) {
            if (stop) {
              action = SleepAction.Break;
            } else if (pause) {
              action = SleepAction.Sleep;
            } else {
              action = SleepAction.Import;
            }
            observableInputStream.ifPresent(in -> in.getCurrentRead());
            lastFileSize = loadingInfo.getFileObject().getContent().getSize();
          }
          if (SleepAction.Sleep == action) {
            Thread.sleep(sleepTime);
          } else if (SleepAction.Break == action) {
            LOGGER.debug("Log import stopped");
            break;
          } else {
            importer.importLogs(loadingInfo.getContentInputStream(), logDataCollector, parsingContext);
            if (!loadingInfo.isTailing() || loadingInfo.isGziped()) {
              break;
            }
          }

          Thread.sleep(sleepTime);
          Utils.reloadFileObject(loadingInfo);
          synchronized (this) {
            observableInputStream.ifPresent(in -> in.getCurrentRead());
            lastFileSize = loadingInfo.getFileObject().getContent().getSize();
          }
          Utils.reloadFileObject(loadingInfo);

        } catch (Exception e) {
          LOGGER.warn("Exception in tailing loop: ", e);
        }
      }
      LOGGER.info(String.format("Loading of files %s is finished", loadingInfo.getFriendlyUrl()));
      parsingContext.setParsingInProgress(false);
      LOGGER.info("File " + loadingInfo.getFriendlyUrl() + " loaded");
      Utils.closeQuietly(loadingInfo.getFileObject());
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.error("Error when reading log", e);
    } finally {
      Utils.closeQuietly(vfs.getFileObject());
    }
  }


  public synchronized LoadStatistic getLoadStatistic() {
    final Long position = observableInputStream.map(ObservableInputStreamImpl::getCurrentRead).orElse(0L);
    return new LoadStatistic(source, position, lastFileSize);
  }


  public synchronized void pause() {
    pause = true;
  }

  public synchronized void resume() {
    pause = false;
  }

  public synchronized void stop() {
    stop = true;
    observableInputStream.ifPresent(ObservableInputStreamImpl::stop);
  }
}
