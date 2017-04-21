package com.idvp.platform.hdfs;

import java.io.IOException;
import java.io.OutputStream;

public interface EventSerializer {
    /**
     * Hook to write a header after file is opened for the first time.
     */
    void afterCreate() throws IOException;

    /**
     * Hook to handle any framing needed when file is re-opened (for write).<br/>
     * Could have been named {@code afterOpenForAppend()}.
     */
    void afterReopen() throws IOException;

    /**
     * Serialize and write the given event.
     *
     * @param data data to write to the underlying stream.
     * @throws IOException
     */
    void write(byte[] data) throws IOException;

    /**
     * Hook to flush any internal write buffers to the underlying stream.
     * It is NOT necessary for an implementation to then call flush() / sync()
     * on the underlying stream itself, since those semantics would be provided
     * by the driver that calls this API.
     */
    void flush() throws IOException;

    /**
     * Hook to write a trailer before the stream is closed.
     * Implementations must not buffer data in this call since
     * EventSerializer.flush() is not guaranteed to be called after beforeClose().
     */
    void beforeClose() throws IOException;

    /**
     * Specify whether this output format supports reopening files for append.
     * For example, this method should return {@code false} if
     * {@link beforeClose()} writes a trailer that "finalizes" the file
     * (this type of behavior is file format-specific).<br/>
     * Could have been named {@code supportsAppend()}.
     */
    boolean supportsReopen();

    /**
     * Knows how to construct this event serializer.<br/>
     * <b>Note: Implementations MUST provide a public a no-arg constructor.</b>
     */
    interface Builder {
        EventSerializer build(OutputStream out);
    }

}
