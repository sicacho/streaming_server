package com.streaming.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Administrator on 6/11/2016.
 */
public class UrlDTO {
  private String file;
  private String label;
  private String type = "mp4";
//  private List<TrackDto> tracks;
  @JsonIgnore
  private Integer resolution;

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getResolution() {
    return resolution;
  }

  public void setResolution(Integer resolution) {
    this.resolution = resolution;
  }

  public UrlDTO(String file, String label) {
    this.file = file;
    this.label = label;
  }

  public UrlDTO() {
  }

  //  public List<TrackDto> getTracks() {
//    return tracks;
//  }
//
//  public void setTracks(List<TrackDto> tracks) {
//    this.tracks = tracks;
//  }
}
