package com.idvp.platform.journal.reader;

import com.idvp.platform.journal.reader.loading.Source;
import com.idvp.platform.journal.reader.loading.VfsSource;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

public class SourceFactory {
  public static Source create(String sourceValue) throws FileSystemException {
    FileObject fileObject = VFS.getManager().resolveFile(sourceValue);
    return new VfsSource(fileObject);
  }
}
