package com.dbs.escaperoom.mediator.controllers;


import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.Resource;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ProxyController {


    protected String proxyTargetURL;
    protected String proxyBase;

    protected String startUrl = "http://escape.daanstreng.nl";

    protected byte[] getResource(String base){
        Resource resource =
                new ClassPathXmlApplicationContext(new String[0]).getResource("url:"+this.proxyTargetURL+base);
        try {
            InputStream sr = resource.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int read;
            byte[] input = new byte[4096];
            while (-1 != (read = sr.read(input))) {
                buffer.write(input, 0, read);
            }
            input = buffer.toByteArray();
            try{
                String html = new String(input);
                if (html !=null){
                    if (html.contains("<html")){
                        if (html.contains("<head>")){
                            html = html.replace("<head>","<head><base href=\""+startUrl+proxyBase+"/\">");
                            input = html.getBytes();
                        }
                        else{
                            html = html.replace("<body","<head><base href=\""+startUrl+proxyBase+"/\" /></head><body");
                        }
                    }
                }
            }catch(Exception ex2){}
            return input;
        }
        catch(Exception ex){}
        return new byte[0];
    }

    @RequestMapping(method = RequestMethod.GET, value = "/view/**", produces = MediaType.ALL_VALUE)
    public byte[] getView(HttpServletRequest request){

        String base = request.getServletPath();
        base = base.replace(proxyBase,"");
        String query = request.getQueryString();
        if (query != null){
            base +="?"+query;
        }
        return getResource(base);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/**")
    public ResponseEntity<Object> getAny(HttpServletRequest request){
        RestTemplate restTemplate = new RestTemplate();
        String base = request.getServletPath();
        base = base.replace(proxyBase,"");
        Boolean sendHtml = false;
        if (base.length()<= 2)
            sendHtml = true;
        String query = request.getQueryString();
        if (query != null){
            base +="?"+query;
        }
        if (sendHtml){
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.TEXT_HTML);
            return new ResponseEntity<Object>(getResource(base),responseHeaders, HttpStatus.OK);
        }
        Object quote = restTemplate.getForObject(this.proxyTargetURL+base, Object.class);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<Object>(quote,responseHeaders, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/**")
    public Object postAny(HttpServletRequest request){
        RestTemplate restTemplate = new RestTemplate();
        String base = request.getServletPath();
        base = base.replace(proxyBase,"");
        String query = request.getQueryString();
        if (query != null){
            base +="?"+query;
        }
        Object quote = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        Map<String, String[]> mapR = request.getParameterMap();
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        for(String k : mapR.keySet()){
            String value = mapR.get(k)[0];
            map.add(k,value);
        }
        HttpEntity<MultiValueMap<String,String>> newRequest = new HttpEntity<MultiValueMap<String, String>>(map,headers);
        System.out.println(base);
        ResponseEntity<Object> response = null;
        try {
            response = restTemplate.exchange(this.proxyTargetURL + base, HttpMethod.POST, newRequest, Object.class);
        }catch (HttpServerErrorException ex){
            return ex.getResponseBodyAsByteArray();
        }
        HttpStatus.Series series = response.getStatusCode().series();
        if( (HttpStatus.Series.CLIENT_ERROR.equals(series)
                || HttpStatus.Series.SERVER_ERROR.equals(series))){
            return response.getBody();
        }
        else return response.getBody();
       // quote = restTemplate.postForEntity(this.proxyTargetURL + base, newRequest, Object.class);
        //return quote;
    }

}
