package com.dbs.escaperoom.mediator.controllers;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@RequestMapping(value = "/**",produces = MediaType.ALL_VALUE)
@RestController
public class ViewController {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<byte[]> getIndex(HttpServletRequest request){
        System.out.println("We get into view");
        try {
            ClassPathResource resource = new ClassPathResource("/static"+request.getServletPath());
            System.out.println("We do mega view it"+request.getServletPath());
            InputStream sr;
            if (resource.exists()&&resource.isReadable()&&request.getServletPath().length()>1) {
                sr = (resource.getInputStream());
                System.out.println("I thought i can read it");
            }
            else
                sr = (new ClassPathResource("/static/index.html").getInputStream());
            byte[] bytes = IOUtils.toByteArray(sr);
            HttpHeaders headers = new HttpHeaders();

            headers.setCacheControl(CacheControl.noCache().getHeaderValue());

            ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, headers, HttpStatus.OK);
            return responseEntity;
        }
        catch(Exception e){}
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(new byte[0], headers, HttpStatus.OK);
        return responseEntity;
    }
}
