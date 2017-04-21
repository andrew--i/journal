package com.idvp.platform.logback.appender.hdfs;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.idvp.platform.hdfs.BucketWriter;
import com.idvp.platform.hdfs.HDFSDataStream;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Data
public class HDFSAppender extends AppenderBase<ILoggingEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HDFSAppender.class);

    private final int defaultCallTimeout = 10000;
    BucketWriter bucketWriter;

    private String filePath;
    private String file = "IDVPLogData";
    private long rollInterval = 10;
    private long rollSize = 1024 * 10;
    private long rollCount = 1024 * 10;
    private long batchSize = 100;
    private String inUsePrefix = "";
    private String inUseSuffix = ".tmp";
    private String fileSuffix = "";
    private ExecutorService callTimeoutPool;
    private ScheduledExecutorService timedRollerPool;
    private int rollTimerPoolSize = 1;
    private int threadsPoolSize = 10;
    private int idleTimeout = 0;
    private long callTimeout = defaultCallTimeout;
    private long retryInterval = 180;
    private int maxCloseTires = Integer.MAX_VALUE;
    private Encoder<ILoggingEvent> encoder;

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (encoder == null) {
            LOGGER.warn("Need specify encoder proerty");
            return;
        }
        try {
            bucketWriter.append(encoder.encode(eventObject));
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Could not append logging event", e);
        }
    }


    @Override
    public void start() {

        String timeoutName = "hdfs-" + getName() + "-call-runner-%d";
        callTimeoutPool = Executors.newFixedThreadPool(threadsPoolSize,
                new ThreadFactoryBuilder().setNameFormat(timeoutName).build());

        String rollerName = "hdfs-" + getName() + "-roll-timer-%d";
        timedRollerPool = Executors.newScheduledThreadPool(rollTimerPoolSize,
                new ThreadFactoryBuilder().setNameFormat(rollerName).build());


        bucketWriter = new BucketWriter(rollInterval, rollSize, rollCount,
                batchSize, filePath, file, inUsePrefix, inUseSuffix,
                fileSuffix, new HDFSDataStream(),
                timedRollerPool, idleTimeout, callTimeout, callTimeoutPool, retryInterval, maxCloseTires);

        super.start();
    }

    @Override
    public void stop() {
        try {
            bucketWriter.close(true);
        } catch (IOException | InterruptedException e) {
            //ignore
        }

        // shut down all our thread pools
        ExecutorService[] toShutdown = {callTimeoutPool, timedRollerPool};
        for (ExecutorService execService : toShutdown) {
            execService.shutdown();
            try {
                while (!execService.isTerminated()) {
                    execService.awaitTermination(
                            Math.max(defaultCallTimeout, callTimeout), TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException ex) {
                //ignore
            }
        }

        callTimeoutPool = null;
        timedRollerPool = null;
        super.stop();
    }
}
