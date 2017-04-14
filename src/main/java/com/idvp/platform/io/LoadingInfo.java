/*******************************************************************************
 * Copyright 2011 Krzysztof Otrebski
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.idvp.platform.io;


import org.apache.commons.vfs2.FileObject;

import java.io.InputStream;

public class LoadingInfo {

  private String friendlyUrl;
  private FileObject fileObject;
  private ObservableInputStreamImpl observableInputStreamImpl;
  private InputStream contentInputStream;
  private boolean tailing;
  private boolean gziped;
  private long lastFileSize = 0;

  public InputStream getContentInputStream() {
    return contentInputStream;
  }

  public void setContentInputStream(InputStream contentInputStream) {
    this.contentInputStream = contentInputStream;
  }

  public boolean isTailing() {
    return tailing;
  }

  public void setTailing(boolean tailing) {
    this.tailing = tailing;
  }

  public boolean isGziped() {
    return gziped;
  }

  public void setGziped(boolean gziped) {
    this.gziped = gziped;
  }

  public String getFriendlyUrl() {
    return friendlyUrl;
  }

  public void setFriendlyUrl(String friendlyUrl) {
    this.friendlyUrl = friendlyUrl;
  }

  public ObservableInputStreamImpl getObservableInputStreamImpl() {
    return observableInputStreamImpl;
  }

  public void setObservableInputStreamImpl(ObservableInputStreamImpl observableInputStreamImpl) {
    this.observableInputStreamImpl = observableInputStreamImpl;
  }

  public FileObject getFileObject() {
    return fileObject;
  }

  public void setFileObject(FileObject fileObject) {
    this.fileObject = fileObject;
  }

  public long getLastFileSize() {
    return lastFileSize;
  }

  public void setLastFileSize(long lastFileSize) {
    this.lastFileSize = lastFileSize;
  }

}
