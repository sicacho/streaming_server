package com.streaming.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
  public ResponseEntity<Resource> download(@PathVariable long id,
                                           @RequestHeader(name = "Range", required = false) String ranger) throws IOException, GeneralSecurityException {
    String urlG = "https://drive.google.com/open?id=0B3YJQgQ5nWc3Mm1sTkxyTXRPSHM";
    GoogleDriveService googleDriveService = new GoogleDriveService();
    String from = "0";
    List<UrlDTO> result;
    com.google.api.client.http.HttpResponse response = null;
    Credential credential = googleDriveService.authorize();
    Drive service = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), null)
        .setHttpRequestInitializer(credential).setApplicationName("432161060989").build();
    if (session.getAttribute(String.valueOf(id)) == null) {
      response = service.getRequestFactory().buildGetRequest(new GenericUrl(urlG)).execute();
      System.out.println("Download At : " + urlG);
      InputStream inputStreamDoc = response.getContent();
      String doc = "";
      int ch = 0;
      while (ch != -1) {

        ch = inputStreamDoc.read();

        if (ch != -1) doc += (char) ch;
      }

      if (ranger == null) {
        ranger = "bytes=0-";
      } else {
        String[] froms = ranger.split("=");
        from = froms[1].replace("-", "");
      }
      result = parseFromHtml(doc, new ArrayList<>());
      session.setAttribute(String.valueOf(id),result);
      session.setAttribute(id+"-cookie",response);
    } else {
      result = (List<UrlDTO>) session.getAttribute(String.valueOf(id));
      response = (HttpResponse) session.getAttribute(id+"-cookie");
    }

    System.out.println("Download at : " + result.get(0).getFile());
    HttpHeaders httpHeadersCookies = new HttpHeaders();
    httpHeadersCookies.set("cookie", response.getHeaders().getHeaderStringValues("set-cookie"));
    httpHeadersCookies.set("Range", Arrays.asList(ranger));
    com.google.api.client.http.HttpResponse response_head = service.getRequestFactory().buildGetRequest(new GenericUrl(result.get(0).getFile())).setHeaders(httpHeadersCookies).execute();
    long content_length = response_head.getHeaders().getContentLength();

    org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
    httpHeaders.setContentLength(content_length);

    httpHeaders.set("Accept-Ranges", response_head.getHeaders().getAccept());
    httpHeaders.set("Content-Range", "bytes " + from + "-" + (content_length - 1) + "/" + content_length);
    httpHeaders.set("Content-Type", response_head.getHeaders().getContentType());
    Resource resource = new InputStreamResource(response_head.getContent());
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
