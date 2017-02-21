package com.streaming.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2/18/2017.
 */
@Getter @Setter
public class FileTemp {
  private long length;
  private byte[] data;
}
