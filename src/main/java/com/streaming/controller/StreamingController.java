package com.streaming.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.*;
import com.streaming.domain.ConvertEntityToDto;
import com.streaming.domain.Resolution;
import com.streaming.domain.StringHelper;
import com.streaming.domain.UrlDTO;
import com.streaming.service.GoogleDriveService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;


/**
 * Created by Administrator on 2/18/2017.
 */
@Controller
public class StreamingController {

  @Autowired
  HttpSession session;

  @RequestMapping(method = {GET, HEAD}, value = "/play/{id}")
  @ResponseBody
  public ResponseEntity<Resource> download(@PathVariable long id,
                                           @RequestHeader(name = "Range", required = false) String ranger) throws IOException, GeneralSecurityException {
    GoogleDriveService googleDriveService = new GoogleDriveService();
    String from = "0";

    Credential credential = googleDriveService.authorize();
    if (ranger == null) {
      ranger = "bytes=0-";
    }
    String finalRanger = ranger;
    Drive service = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), null).setHttpRequestInitializer(credential).setApplicationName("432161060989").build();
    com.google.api.services.drive.model.File model = service.files().get("0B6iOGhAfgoxVSE5qWEo1QW1sakk").setFields("size").execute();
    long content_length = model.getSize();
    HttpHeaders header = new HttpHeaders();
    header.set("Accept-Ranges", "bytes");
    header.setRange("bytes=0-");
    Drive.Files.Get get = service.files().get("0B6iOGhAfgoxVSE5qWEo1QW1sakk");
    get.setRequestHeaders(header);
    InputStream stream = get.executeMediaAsInputStream();
    long start = System.currentTimeMillis();
    org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
    httpHeaders.set("content-Length", String.valueOf(content_length));
    httpHeaders.set("accept-ranges", "bytes");
    httpHeaders.set("content-range", "bytes " + from + "-" + (content_length - 1) + "/" + content_length);
    httpHeaders.set("content-type", "application/octet-stream");
    Resource resource = new InputStreamResource(stream);
    long end = System.currentTimeMillis();
    System.out.println("Time : " + (start-end));
    ResponseEntity<Resource> resourceResponseEntity = ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(httpHeaders).body(resource);
    return resourceResponseEntity;
  }

  private List<UrlDTO> parseFromHtml(String doc, List<UrlDTO> result) {
    Document document = Jsoup.parse(doc);
    Elements scriptElements = document.getElementsByTag("script");
    String text = scriptElements.get(scriptElements.size() - 2).dataNodes().get(0).getWholeData();
    Pattern pattern = Pattern.compile("(?=fmt_stream_map\\\",\\\")(.*)(?=\\\")");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      String[] listUrl = matcher.group(1).replace("\"", "").split("\\,");
      result = Arrays.stream(listUrl).
          filter(s -> StringHelper.isNotStartwith(s, Resolution.values())).
          map(s1 -> ConvertEntityToDto.urlDTO(StringEscapeUtils.unescapeJava(s1))).
          collect(Collectors.toList());
      Collections.reverse(result);
    }
    return result;
  }

  @RequestMapping(method = {GET, HEAD}, value = "/playgoogle")
  @ResponseBody
  private List<UrlDTO> playGoogle(@RequestParam String googleId) {
    List<UrlDTO> result = null;
    try {
      URL url = new URL("https://docs.google.com/get_video_info?mobile=true&docid="+googleId+"&authuser=0");
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
      urlConnection.setRequestProperty("Referer","https://drive.google.com/drive/u/0/my-drive");
      BufferedReader in = new BufferedReader(
              new InputStreamReader(urlConnection.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      String doc = response.toString();
      String[] params = doc.split("&");
      String linkRaw = Arrays.stream(params).
              filter(s -> s.startsWith("fmt_stream_map=")).
              map(s1 -> s1.replace("fmt_stream_map=","")).findFirst().get();
      String[] links = null;
      try {
        links = URLDecoder.decode(linkRaw,"UTF-8").split(",");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      if(links!=null) {
        result = Arrays.stream(links).
                filter(s -> StringHelper.isNotStartwith(s, Resolution.values())).
                map(s1 -> ConvertEntityToDto.urlDTO(StringEscapeUtils.unescapeJava(s1))).
                collect(Collectors.toList());
        Collections.sort(result,(o1, o2) -> o1.getResolution()-o2.getResolution());
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }
}
