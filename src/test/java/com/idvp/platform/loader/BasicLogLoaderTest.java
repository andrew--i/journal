package com.idvp.platform.loader;

import com.idvp.platform.collector.ProxyLogDataCollector;
import com.idvp.platform.importer.LogImporterUsingParser;
import com.idvp.platform.loading.VfsSource;
import com.idvp.platform.parser.simple.LineLogParser;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.awaitility.Awaitility.await;

public class BasicLogLoaderTest {

  LogLoader logLoader;

  @Before
  public void setUp() throws Exception {
    logLoader = new BasicLogLoader();
  }

  @After
  public void tearDown() throws Exception {
    logLoader.shutdown();
  }

  private FileObject createLocalFileObject(String fileName) throws FileSystemException {
    URL resource = getClass().getClassLoader().getResource(fileName);
    return VFS.getManager().resolveFile(resource);
  }

  private void writeToFileObject(FileObject fileObject) throws IOException {
    try (OutputStream outputStream = fileObject.getContent().getOutputStream(true)) {
      try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {
        bufferedWriter.write("Current time is " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bufferedWriter.newLine();
      }
    }
  }

  @Test
  public void testLogLoaderApi() throws Exception {

    ProxyLogDataCollector logDataCollector = new ProxyLogDataCollector();
    FileObject localFileObject = createLocalFileObject("checkApi.txt");
    localFileObject.delete();
    localFileObject.createFile();
    logLoader.startLoading(new VfsSource(localFileObject), new LogImporterUsingParser(new LineLogParser()), logDataCollector);
    Assert.assertEquals(0, logDataCollector.getLogData().length);
    writeToFileObject(localFileObject);
    await().until(() -> logDataCollector.getLogData().length > 0);
    Assert.assertEquals(1, logDataCollector.getLogData().length);

  }
}