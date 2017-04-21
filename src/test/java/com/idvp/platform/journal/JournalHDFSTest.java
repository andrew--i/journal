package com.idvp.platform.journal;

import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.awaitility.Duration;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;


@Ignore("Integration test!!! Be careful! See HDFSAppenderTest.deleteAllTestFiles")
public class JournalHDFSTest extends JournalTestBase {


    @Override
    protected String getJournalFile() {
        return "string.journal";
    }

    @Override
    protected String getLogbackConfigFile() {
        return "journal_hdfs/logback.xml";
    }

    @Override
    protected String getJournalConfigPath() {
        return "journal_hdfs/journal.config.xml";
    }

    @Override
    protected File getJournalPath() {
        return new File("webhdfs:////localhost:50070/");
    }

    @Override
    protected String getTestPathFor(String file) {
        return "webhdfs:////localhost:50070/" + file;
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        Configuration conf = new Configuration();
        Path dstPath = new Path("webhdfs://localhost:50070/");
        FileSystem hdfs = dstPath.getFileSystem(conf);
        final RemoteIterator<LocatedFileStatus> files = hdfs.listFiles(dstPath, true);
        while (files.hasNext()) {
            final LocatedFileStatus fileStatus = files.next();
            if (fileStatus.getPath().getName().contains(".jounral"))
                hdfs.delete(fileStatus.getPath(), true);
        }
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final StandardFileSystemManager manager = (StandardFileSystemManager) VFS.getManager();
        final URL providers = Thread.currentThread().getContextClassLoader().getResource("journal_hdfs/providers.xml");
        manager.setConfiguration(providers);
        manager.init();
    }

    @Test
    public void testJournalApi() throws Exception {
        String message = "some record at " + System.currentTimeMillis();
        journalProvider.write(message);
        Thread.sleep(10000);
        assertEquals(message, journalProvider.read(String.class).iterator().next());
    }
}