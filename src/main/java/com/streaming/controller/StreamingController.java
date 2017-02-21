package com.streaming.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;


/**
 * Created by Administrator on 2/18/2017.
 */
@Controller
public class StreamingController {

  @RequestMapping(method = {GET,HEAD},value = "/play/{id}")
  public ResponseEntity<Resource> download(@PathVariable long id,
                                           @RequestHeader(name = "Range") String ranger) {

//    Resource resource = new ByteArrayResource();
    return null;
  }
}
