package com.idvp.platform.journal.reader;

import com.idvp.platform.journal.JournalRecordTransformer;
import com.idvp.platform.journal.reader.collector.LogDataCollector;
import com.idvp.platform.journal.reader.importer.LogImporterUsingParser;
import com.idvp.platform.journal.reader.loader.BasicLogLoader;
import com.idvp.platform.journal.reader.loading.LogLoadingSession;
import com.idvp.platform.journal.reader.loading.Source;
import com.idvp.platform.journal.reader.loading.VfsSource;
import com.idvp.platform.journal.reader.parser.LogParser;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JournalRecordsReader<T> implements FileListener {
  private BasicLogLoader logLoader = new BasicLogLoader();
  private Source source;
  private JournalRecordCollector<T> journalRecordCollector;
  private LogParser logParser;
  private String name;
  private DefaultFileMonitor journalDirectoryMonitor;
  private List<LogLoadingSession> loadingSessions = new ArrayList<>();

  public JournalRecordsReader(Source source, Class<T> tClass, LogDataCollector logDataCollector) {
    JournalRecordTransformer<T> transformer = new JournalRecordTransformer<>(tClass);
    this.journalRecordCollector = new JournalRecordCollector<>(transformer, logDataCollector);
    this.logParser = new JournalRecordParser<>(transformer);
    this.source = source;
  }

  public void open() {
    if (source == null)
      throw new IllegalArgumentException("Could not open journal with nullable source");
    List<Source> sources;
    try {
      sources = getSourcesIfSourceIsDirectory(source);
    } catch (FileSystemException e) {
      throw new IllegalArgumentException("Could not open source", e);
    }
    sources.forEach(this::startLoading);
  }

  private List<Source> getSourcesIfSourceIsDirectory(Source rootSource) throws FileSystemException {
    if (rootSource instanceof VfsSource) {
      FileObject fileObject = ((VfsSource) rootSource).getFileObject();
      if (fileObject.getType() == FileType.FOLDER) {
        FileObject[] journalSources = fileObject.findFiles(new FileExtensionSelector("journal"));
        journalDirectoryMonitor = new DefaultFileMonitor(this);
        journalDirectoryMonitor.setRecursive(true);
        journalDirectoryMonitor.addFile(((VfsSource) rootSource).getFileObject());
        journalDirectoryMonitor.start();
        return Arrays.stream(journalSources).map(VfsSource::new).collect(Collectors.toList());
      } else
        return Collections.singletonList(rootSource);
    } else
      return Collections.singletonList(rootSource);
  }

  public void close() {
    logLoader.shutdown();
    if (journalDirectoryMonitor != null) {
      journalDirectoryMonitor.stop();
      journalDirectoryMonitor = null;
    }
  }

  public JournalRecordCollector<T> getJournalRecordCollector() {
    return journalRecordCollector;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  private void startLoading(Source s) {
    LogLoadingSession logLoadingSession = logLoader.startLoading(s, new LogImporterUsingParser(logParser), journalRecordCollector);
    loadingSessions.add(logLoadingSession);
  }

  @Override
  public synchronized void fileCreated(FileChangeEvent event) throws Exception {
    if (event.getFile().getType() == FileType.FILE)
      startLoading(new VfsSource(event.getFile()));
  }

  @Override
  public synchronized void fileDeleted(FileChangeEvent event) throws Exception {
    FileObject file = event.getFile();
    List<LogLoadingSession> sessions = new ArrayList<>();
    for (LogLoadingSession loadingSession : this.loadingSessions) {
      Source s = loadingSession.getSource();
      if (s instanceof VfsSource) {
        if (((VfsSource) s).getFileObject().getURL().equals(file.getURL()))
          sessions.add(loadingSession);
      }
    }

    for (LogLoadingSession loadingSession : sessions) {
      logLoader.close(loadingSession);
      loadingSessions.remove(loadingSession);
    }
  }

  @Override
  public void fileChanged(FileChangeEvent event) throws Exception {

  }
}
