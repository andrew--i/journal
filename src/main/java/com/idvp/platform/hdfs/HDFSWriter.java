package com.idvp.platform.hdfs;

import java.io.IOException;

public interface HDFSWriter {
    void open(String filePath) throws IOException;

    void append(byte[] data) throws IOException;

    void sync() throws IOException;

    void close() throws IOException;

    boolean isUnderReplicated();
}
