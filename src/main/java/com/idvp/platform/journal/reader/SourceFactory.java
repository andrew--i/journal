package com.idvp.platform.journal.reader;

import com.idvp.platform.journal.reader.loading.VfsSource;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

import java.io.File;
import java.net.URI;

public class SourceFactory {
    public static VfsSource create(String sourceValue, String key) throws FileSystemException {
        String normalizeString = sourceValue.replace("/", File.separator);
        normalizeString = normalizeString.replace("\\", File.separator);
        int i = normalizeString.lastIndexOf(File.separator);
        String newSourceValue = normalizeString.substring(0, i) + File.separator + key + File.separator + normalizeString.substring(i + 1);
        FileObject fileObject = VFS.getManager().resolveFile(newSourceValue);
        return new VfsSource(fileObject);
    }
}
