package com.idvp.platform.logback.appender.hdfs;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.apache.commons.vfs2.VFS;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

@Ignore("Integration test!!! Be careful! See HDFSAppenderTest.deleteAllTestFiles")
public class HDFSAppenderTest {

    private void configureLogging() throws IOException, JoranException {
        String logbackConfigFile = getLogbackConfigFile();

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(logbackConfigFile)) {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            context.reset();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            configurator.doConfigure(inputStream);

        }
    }

    private String getLogbackConfigFile() {
        return "hdfs/logback.xml";
    }

    @Before
    public void setUp() throws Exception {
        configureLogging();
    }

    private void deleteAllTestJournalFiles() throws IOException {
        Configuration conf = new Configuration();
        Path dstPath = new Path("webhdfs://localhost:50070/");
        FileSystem hdfs = dstPath.getFileSystem(conf);
        final RemoteIterator<LocatedFileStatus> files = hdfs.listFiles(dstPath, true);
        while (files.hasNext()) {
            final LocatedFileStatus fileStatus = files.next();
            if (fileStatus.getPath().getName().contains(".tjounral"))
                hdfs.delete(fileStatus.getPath(), true);
        }
    }

    @After
    public void tearDown() throws Exception {
        VFS.getManager().getFilesCache().close();
        deleteAllTestJournalFiles();
    }

    @Test
    public void testHDFSAppender() throws Exception {
        final Logger hdfsLogger = LoggerFactory.getLogger("hdfsLogger");
        Assert.assertNotNull(hdfsLogger);
        hdfsLogger.info("hello world!!!");

        ((LoggerContext) LoggerFactory.getILoggerFactory()).stop();

        Configuration conf = new Configuration();
        Path dstPath = new Path("webhdfs://localhost:50070/");
        FileSystem hdfs = dstPath.getFileSystem(conf);
        final RemoteIterator<LocatedFileStatus> files = hdfs.listFiles(dstPath, true);
        Assert.assertNotNull(files);
        Assert.assertTrue(files.hasNext());

    }
}