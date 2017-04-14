package com.idvp.platform.journal.configuration;

import com.idvp.platform.loading.Source;
import com.idvp.platform.loading.VfsSource;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.util.Optional;

public class JournalConfigurationProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(JournalConfigurationProvider.class);

	public Optional<Source> createSourceByJournalKey(String key) {
		try {
			final URL resource = Thread.currentThread().getContextClassLoader().getResource(".");
			final FileObject fileObject = VFS.getManager().resolveFile(URI.create(resource.getPath() + "sample_journal.txt"));
			if(!fileObject.exists())
				fileObject.createFile();
			return Optional.of(new VfsSource(fileObject));
		} catch (FileSystemException e) {
			LOGGER.warn("Could not create journal source", e);
			return Optional.empty();
		}
	}
}
