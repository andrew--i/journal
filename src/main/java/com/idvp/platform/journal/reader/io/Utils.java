/*
 * Copyright 2012 Krzysztof Otrebski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.idvp.platform.journal.reader.io;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.RandomAccessContent;
import org.apache.commons.vfs2.util.RandomAccessMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class Utils {

  private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class.getName());

  private static boolean checkIfIsGzipped(byte[] buffer, int lenght) throws IOException {
    boolean gziped;
    try {
      ByteArrayInputStream bin = new ByteArrayInputStream(buffer, 0, lenght);
      GZIPInputStream gzipInputStream = new GZIPInputStream(bin);
      gzipInputStream.read(new byte[buffer.length], 0, bin.available());
      gziped = true;
    } catch (IOException e) {
      gziped = false;
    }
    return gziped;
  }

  public static byte[] loadProbe(InputStream in, int buffSize) throws IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    byte[] buff = new byte[buffSize];
    int read = in.read(buff);
    if (read > 0) {
      bout.write(buff, 0, read);
    }

    return bout.toByteArray();
  }


  public static LoadingInfo openFileObject(FileObject fileObject, boolean tailing) throws Exception {
    LoadingInfo loadingInfo = new LoadingInfo();
    loadingInfo.setFileObject(fileObject);
    loadingInfo.setFriendlyUrl(fileObject.getName().getFriendlyURI());

    final FileContent content = fileObject.getContent();
    InputStream httpInputStream = content.getInputStream();
    byte[] buff = Utils.loadProbe(httpInputStream, 10000);

    loadingInfo.setGziped(checkIfIsGzipped(buff, buff.length));

    ByteArrayInputStream bin = new ByteArrayInputStream(buff);

    SequenceInputStream sequenceInputStream = new SequenceInputStream(bin, httpInputStream);

    ObservableInputStreamImpl observableInputStreamImpl = new ObservableInputStreamImpl(sequenceInputStream);

    if (loadingInfo.isGziped()) {
      loadingInfo.setContentInputStream(new GZIPInputStream(observableInputStreamImpl));
    } else {
      loadingInfo.setContentInputStream(observableInputStreamImpl);
    }
    loadingInfo.setObservableInputStreamImpl(observableInputStreamImpl);

    loadingInfo.setTailing(tailing);
    if (fileObject.getType().hasContent()) {
      loadingInfo.setLastFileSize(content.getSize());
    }
    return loadingInfo;

  }

  public static void reloadFileObject(LoadingInfo loadingInfo, long position) throws IOException {
    loadingInfo.getFileObject().refresh();
    long currentSize = loadingInfo.getFileObject().getContent().getSize();
    IOUtils.closeQuietly(loadingInfo.getObservableInputStreamImpl());
    RandomAccessContent randomAccessContent = loadingInfo.getFileObject().getContent().getRandomAccessContent(RandomAccessMode.READ);
    randomAccessContent.seek(position);
    loadingInfo.setLastFileSize(currentSize);
    ObservableInputStreamImpl observableStream = new ObservableInputStreamImpl(randomAccessContent.getInputStream(), 0);
    loadingInfo.setObservableInputStreamImpl(observableStream);
    if (loadingInfo.isGziped()) {
      loadingInfo.setContentInputStream(new GZIPInputStream(observableStream));
    } else {
      loadingInfo.setContentInputStream(observableStream);
    }
  }

  public static void reloadFileObject(LoadingInfo loadingInfo) throws IOException {
    loadingInfo.getFileObject().refresh();
    long lastFileSize = loadingInfo.getLastFileSize();
    long currentSize = loadingInfo.getFileObject().getContent().getSize();
    if (currentSize > lastFileSize) {
      IOUtils.closeQuietly(loadingInfo.getObservableInputStreamImpl());

      RandomAccessContent randomAccessContent = loadingInfo.getFileObject().getContent().getRandomAccessContent(RandomAccessMode.READ);
      randomAccessContent.seek(lastFileSize);
      loadingInfo.setLastFileSize(currentSize);
      ObservableInputStreamImpl observableStream = new ObservableInputStreamImpl(randomAccessContent.getInputStream(), lastFileSize);
      loadingInfo.setObservableInputStreamImpl(observableStream);
      if (loadingInfo.isGziped()) {
        loadingInfo.setContentInputStream(new GZIPInputStream(observableStream));
      } else {
        loadingInfo.setContentInputStream(observableStream);
      }
    } else if (currentSize < lastFileSize) {
      IOUtils.closeQuietly(loadingInfo.getObservableInputStreamImpl());
      InputStream inputStream = loadingInfo.getFileObject().getContent().getInputStream();
      ObservableInputStreamImpl observableStream = new ObservableInputStreamImpl(inputStream, 0);
      loadingInfo.setObservableInputStreamImpl(observableStream);
      if (loadingInfo.isGziped()) {
        loadingInfo.setContentInputStream(new GZIPInputStream(observableStream));
      } else {
        loadingInfo.setContentInputStream(observableStream);
      }
      loadingInfo.setLastFileSize(loadingInfo.getFileObject().getContent().getSize());
    }

  }

  public static void closeQuietly(FileObject fileObject) {

    if (fileObject != null) {
      String friendlyURI = fileObject.getName().getFriendlyURI();
      try {
        LOGGER.info(String.format("Closing file %s", friendlyURI));
        fileObject.close();
        LOGGER.info(String.format("File %s closed", friendlyURI));
      } catch (FileSystemException ignore) {
        LOGGER.error(String.format("File %s is not closed: %s", friendlyURI, ignore.getMessage()));
      }
    }
  }
}
