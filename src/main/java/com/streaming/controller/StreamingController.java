package com.streaming.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.*;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.streaming.domain.ConvertEntityToDto;
import com.streaming.domain.Resolution;
import com.streaming.domain.StringHelper;
import com.streaming.domain.UrlDTO;
import com.streaming.service.GoogleDriveService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
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

  @RequestMapping(method = {GET, HEAD}, value = "/play/{id}")
  public ResponseEntity<Resource> download(@PathVariable long id,
                                           @RequestHeader(name = "Range",required = false) String ranger) throws IOException, GeneralSecurityException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1600000);
    String urlG = "https://drive.google.com/open?id=0B3YJQgQ5nWc3Mm1sTkxyTXRPSHM";
    GoogleDriveService googleDriveService = new GoogleDriveService();
    Credential credential = googleDriveService.authorize();
    Drive service = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), null)
            .setHttpRequestInitializer(credential).setApplicationName("432161060989").build();
    com.google.api.client.http.HttpResponse response = service.getRequestFactory().buildGetRequest(new GenericUrl(urlG)).execute();
    System.out.println("Download At : " + urlG);
    InputStream inputStreamDoc = response.getContent();
    String doc = "";
    int ch = 0;
    while (ch != -1) {

      ch = inputStreamDoc.read();

      if (ch != -1) doc += (char) ch;
    }
    List<UrlDTO> result = parseFromHtml(doc, new ArrayList<>());;
    System.out.println("Download at : " + result.get(0).getFile());
    HttpHeaders httpHeadersCookies = new HttpHeaders();
    httpHeadersCookies.set("cookie", response.getHeaders().getHeaderStringValues("set-cookie"));
    com.google.api.client.http.HttpResponse response_head = service.getRequestFactory().buildHeadRequest(new GenericUrl( result.get(0).getFile())).setHeaders(httpHeadersCookies).execute();
    long content_length = response_head.getHeaders().getContentLength();
    org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
    httpHeaders.setContentLength(content_length);
    httpHeaders.set("Accept-Ranges",response_head.getHeaders().getAccept());
    httpHeaders.set("Content-Range",response_head.getHeaders().getContentRange());
    httpHeaders.set("Content-Type",response_head.getHeaders().getContentType());
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          HttpTransport httpTransport = new ApacheHttpTransport();
          MediaHttpDownloader downloader = new MediaHttpDownloader(httpTransport, httpTransport.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
              HttpHeaders httpHeaders = new HttpHeaders();
              httpHeaders.set("cookie", response.getHeaders().getHeaderStringValues("set-cookie"));
              request.setHeaders(httpHeaders);
            }
          }).getInitializer());
          downloader.setChunkSize(400000);
          downloader.download(new GenericUrl(result.get(0).getFile()), outputStream);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    Thread thread = new Thread(runnable);
    thread.start();
    Resource resource = new ByteArrayResource(outputStream.toByteArray());
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
}
