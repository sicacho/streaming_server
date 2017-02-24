package com.streaming.domain;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Administrator on 4/15/2016.
 */
public class ConvertEntityToDto {


  public static UrlDTO urlDTO(String url) {
    UrlDTO urlDTO = new UrlDTO();
    if (url.startsWith(Resolution.RE_1080.toString())) {
      urlDTO.setLabel("1080");
      urlDTO.setResolution(1080);
    } else if (url.startsWith(Resolution.RE_720.toString())) {
      urlDTO.setLabel("720");
      urlDTO.setResolution(720);
    } else if (url.startsWith(Resolution.RE_480.toString())) {
      urlDTO.setLabel("480");
      urlDTO.setResolution(480);
    } else if (url.startsWith(Resolution.RE_360.toString())) {
      urlDTO.setLabel("360");
      urlDTO.setResolution(360);
    }
    urlDTO.setFile((url.split("\\|")[1]));
    urlDTO.setFile(urlDTO.getFile().replace("ipbits=48", "ipbits=0"));
    urlDTO.setFile(urlDTO.getFile().replace("ipbits=24", "ipbits=0"));
    String urlhead = "https://redirector.googlevideo.com";
    String redirectorUrl = urlhead + "/videoplayback" + urlDTO.getFile().split("/videoplayback")[1];
    String[] params = redirectorUrl.split("&");
    String redirectorURL_NoId = Stream.of(params).filter(s -> !s.toLowerCase().startsWith("driveid=")).collect(Collectors.joining("&"));
    urlDTO.setFile(redirectorURL_NoId);
    return urlDTO;
  }
}
