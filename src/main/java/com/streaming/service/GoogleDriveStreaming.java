package com.streaming.service;

import com.streaming.domain.FileTemp;

/**
 * Created by Administrator on 2/21/2017.
 */
public class GoogleDriveStreaming implements DownloadService {
  @Override
  public byte[] download(String id, long ranger) {
    return null;
  }

  @Override
  public long getLength(String id) {
    return 0;
  }
}
