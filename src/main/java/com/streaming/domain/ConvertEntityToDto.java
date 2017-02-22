package com.streaming.domain;

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
//    urlDTO.setFile(urlDTO.getFile().replace("ipbits=48", "ipbits=0"));
//    urlDTO.setFile(urlDTO.getFile().replace("ipbits=24", "ipbits=0"));
//    String urlhead = "https://redirector.googlevideo.com";
//    urlDTO.setFile(urlhead+"/videoplayback"+urlDTO.getFile().split("/videoplayback")[1]);
//        List<TrackDto> trackDtos = new ArrayList<>();
//        trackDtos.add(new TrackDto("thumbnails","https://doc-04-3k-docs.googleusercontent.com/docs/securesc/ha0ro937gcuc7l7deffksulhg5h7mbp1/jlsji0ad22meqajtku5sduo9stmm8n08/1480233600000/17129975411737702559/*/0B3YJQgQ5nWc3RHZPRWVLd0t6Ykk?e=download"));
//        urlDTO.setTracks(trackDtos);
    return urlDTO;
  }
}
