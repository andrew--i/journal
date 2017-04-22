package com.idvp.platform.journal.reader.loader;


import com.idvp.platform.journal.JournalTestBase;
import com.idvp.platform.journal.reader.collector.ProxyLogDataCollector;
import com.idvp.platform.journal.reader.importer.LogImporterUsingParser;
import com.idvp.platform.journal.reader.loading.VfsSource;
import com.idvp.platform.journal.reader.parser.simple.LineLogParser;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.awaitility.Awaitility.await;

public class BasicLogLoaderTest extends JournalTestBase {

    LogLoader logLoader;

    @Before
    public void setUp() throws Exception {
        logLoader = new BasicLogLoader();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        logLoader.shutdown();
    }

    private FileObject createLocalFileObject(String fileName) throws IOException {
        final File file = new File(fileName);
        if (file.exists())
            file.delete();
        file.createNewFile();

        return VFS.getManager().resolveFile(file.getAbsolutePath());
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
        FileObject localFileObject = createLocalFileObject(getTestPathFor("checkApi.txt"));
        logLoader.startLoading(new VfsSource(localFileObject), new LogImporterUsingParser(new LineLogParser()), logDataCollector);
        Assert.assertEquals(0, logDataCollector.getLogData().length);
        writeToFileObject(localFileObject);
        await().until(() -> logDataCollector.getLogData().length > 0);
        Assert.assertEquals(1, logDataCollector.getLogData().length);
        localFileObject.delete();
    }
}