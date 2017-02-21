package com.streaming.service;

import com.streaming.domain.FileTemp;

/**
 * Created by Administrator on 2/18/2017.
 */
public interface DownloadService {
  public byte[] download(String id, long ranger);
  public long getLength(String id);
}
