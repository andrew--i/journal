package com.idvp.platform.journal.reader;

import com.idvp.platform.journal.reader.loading.VfsSource;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

import java.io.File;
import java.net.URI;

public class SourceFactory {
    public static VfsSource create(String sourceValue, String key) throws FileSystemException {
        FileObject fileObject;

        try {
            String newSourceValue = sourceValue + "/" + key + "/";
            final URI normalize = URI.create(newSourceValue).normalize();
            fileObject = VFS.getManager().resolveFile(normalize);
        } catch (IllegalArgumentException e) {
            String value = sourceValue.replace("\\", File.separator).replace("/", File.separator);
            if(!value.endsWith(File.separator))
                value = value + File.separator;
            fileObject = VFS.getManager().resolveFile(value + File.separator + key + File.separator);
        }
        return new VfsSource(fileObject);
    }
}
