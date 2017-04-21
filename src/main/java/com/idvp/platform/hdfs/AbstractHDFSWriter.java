package com.idvp.platform.hdfs;

import com.google.common.base.Preconditions;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AbstractHDFSWriter implements HDFSWriter {
    static final Object[] NO_ARGS = new Object[]{};
    private static final Logger logger = LoggerFactory.getLogger(AbstractHDFSWriter.class);
    private FSDataOutputStream outputStream;
    private FileSystem fs;
    private Path destPath;
    private Method refGetNumCurrentReplicas = null;
    private Method refGetDefaultReplication = null;
    private Method refHflushOrSync = null;
    private Integer configuredMinReplicas = null;


    protected void registerCurrentStream(FSDataOutputStream outputStream,
                                         FileSystem fs, Path destPath) {
        Preconditions.checkNotNull(outputStream, "outputStream must not be null");
        Preconditions.checkNotNull(fs, "fs must not be null");
        Preconditions.checkNotNull(destPath, "destPath must not be null");

        this.outputStream = outputStream;
        this.fs = fs;
        this.destPath = destPath;
        this.refGetNumCurrentReplicas = reflectGetNumCurrentReplicas(outputStream);
        this.refGetDefaultReplication = reflectGetDefaultReplication(fs);
        this.refHflushOrSync = reflectHflushOrSync(outputStream);

    }

    /**
     * Find the 'getNumCurrentReplicas' on the passed <code>os</code> stream.
     *
     * @return Method or null.
     */
    private Method reflectGetNumCurrentReplicas(FSDataOutputStream os) {
        Method m = null;
        if (os != null) {
            Class<? extends OutputStream> wrappedStreamClass = os.getWrappedStream()
                    .getClass();
            try {
                m = wrappedStreamClass.getDeclaredMethod("getNumCurrentReplicas",
                        new Class<?>[]{});
                m.setAccessible(true);
            } catch (NoSuchMethodException e) {
                logger.info("FileSystem's output stream doesn't support"
                        + " getNumCurrentReplicas; --HDFS-826 not available; fsOut="
                        + wrappedStreamClass.getName() + "; err=" + e);
            } catch (SecurityException e) {
                logger.info("Doesn't have access to getNumCurrentReplicas on "
                        + "FileSystems's output stream --HDFS-826 not available; fsOut="
                        + wrappedStreamClass.getName(), e);
                m = null; // could happen on setAccessible()
            }
        }
        if (m != null) {
            logger.debug("Using getNumCurrentReplicas--HDFS-826");
        }
        return m;
    }

    /**
     * Find the 'getDefaultReplication' method on the passed <code>fs</code>
     * FileSystem that takes a Path argument.
     *
     * @return Method or null.
     */
    private Method reflectGetDefaultReplication(FileSystem fileSystem) {
        Method m = null;
        if (fileSystem != null) {
            Class<?> fsClass = fileSystem.getClass();
            try {
                m = fsClass.getMethod("getDefaultReplication",
                        new Class<?>[]{Path.class});
            } catch (NoSuchMethodException e) {
                logger.debug("FileSystem implementation doesn't support"
                        + " getDefaultReplication(Path); -- HADOOP-8014 not available; " +
                        "className = " + fsClass.getName() + "; err = " + e);
            } catch (SecurityException e) {
                logger.debug("No access to getDefaultReplication(Path) on "
                        + "FileSystem implementation -- HADOOP-8014 not available; " +
                        "className = " + fsClass.getName() + "; err = " + e);
            }
        }
        if (m != null) {
            logger.debug("Using FileSystem.getDefaultReplication(Path) from " +
                    "HADOOP-8014");
        }
        return m;
    }

    private Method reflectHflushOrSync(FSDataOutputStream os) {
        Method m = null;
        if (os != null) {
            Class<?> fsDataOutputStreamClass = os.getClass();
            try {
                m = fsDataOutputStreamClass.getMethod("hflush");
            } catch (NoSuchMethodException ex) {
                logger.debug("HFlush not found. Will use sync() instead");
                try {
                    m = fsDataOutputStreamClass.getMethod("sync");
                } catch (Exception ex1) {
                    String msg = "Neither hflush not sync were found. That seems to be " +
                            "a problem!";
                    logger.error(msg);
                }
            }
        }
        return m;
    }

    public boolean isUnderReplicated() {
        try {
            int numBlocks = getNumCurrentReplicas();
            if (numBlocks == -1) {
                return false;
            }
            int desiredBlocks;
            if (configuredMinReplicas != null) {
                desiredBlocks = configuredMinReplicas;
            } else {
                desiredBlocks = getFsDesiredReplication();
            }
            return numBlocks < desiredBlocks;
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            logger.error("Unexpected error while checking replication factor", e);
        }
        return false;
    }

    public int getFsDesiredReplication() {
        short replication = 0;
        if (fs != null && destPath != null) {
            if (refGetDefaultReplication != null) {
                try {
                    replication = (Short) refGetDefaultReplication.invoke(fs, destPath);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.warn("Unexpected error calling getDefaultReplication(Path)", e);
                }
            } else {
                // will not work on Federated HDFS (see HADOOP-8014)
                replication = fs.getDefaultReplication();
            }
        }
        return replication;
    }

    public int getNumCurrentReplicas()
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        if (refGetNumCurrentReplicas != null && outputStream != null) {
            OutputStream dfsOutputStream = outputStream.getWrappedStream();
            if (dfsOutputStream != null) {
                Object repl = refGetNumCurrentReplicas.invoke(dfsOutputStream, NO_ARGS);
                if (repl instanceof Integer) {
                    return ((Integer) repl).intValue();
                }
            }
        }
        return -1;
    }

    /**
     * If hflush is available in this version of HDFS, then this method calls
     * hflush, else it calls sync.
     *
     * @param os - The stream to flush/sync
     * @throws IOException
     */
    protected void hflushOrSync(FSDataOutputStream os) throws IOException {
        try {
            // At this point the refHflushOrSync cannot be null,
            // since register method would have thrown if it was.
            this.refHflushOrSync.invoke(os);
        } catch (InvocationTargetException e) {
            String msg = "Error while trying to hflushOrSync!";
            logger.error(msg);
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof IOException) {
                throw (IOException) cause;
            }
        } catch (Exception e) {
            String msg = "Error while trying to hflushOrSync!";
            logger.error(msg);
        }
    }

    protected void unregisterCurrentStream() {
        this.outputStream = null;
        this.fs = null;
        this.destPath = null;
        this.refGetNumCurrentReplicas = null;
        this.refGetDefaultReplication = null;
    }
}
