package com.dbs.escaperoom.mediator.controllers;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RequestMapping(value = "/room/{uuid}",produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class RoomProxyController {
    protected String startUrl = "http://escape.daanstreng.nl";
    @RequestMapping(method = RequestMethod.GET, value = "/view/**",produces = MediaType.ALL_VALUE)
    public byte[] getView(HttpServletRequest request,@PathVariable String uuid){
        RestTemplate restTemplate = new RestTemplate();
        String base = request.getServletPath();
        base = base.replace("/room/"+uuid,"");
        String query = request.getQueryString();
        if (query != null){
            base +="?"+query;
        }
        Map<String,String> returnMap = restTemplate.getForObject("http://localhost:7001/registration/getbyuuid/"+uuid, Map.class);
        return  getResourceOnURL(returnMap.get("endPoint")+base,uuid);

    }
    private byte[] getResourceOnURL(String url, String uuid){
        Resource resource =
                new ClassPathXmlApplicationContext(new String[0]).getResource("url:"+url);
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
                    if (html.contains("<html>")){
                        if (html.contains("<head>")){
                            html = html.replace("<head>","<head><base href=\""+startUrl+"/room/"+uuid+"/\" >");
                            input = html.getBytes();
                        }
                        else{
                            html = html.replace("<html>","<html><head><base href=\"+startUrl+\"/room/"+uuid+"/\" ></head>");
                        }
                    }
                }
            }catch(Exception ex2){}
            return input;
        }
        catch(Exception ex){}
        return new byte[0];
    }

    @RequestMapping(method = RequestMethod.GET, value = "/**")
    public Object getAny(HttpServletRequest request,@PathVariable String uuid){
        RestTemplate restTemplate = new RestTemplate();
        String base = request.getServletPath();
        base = base.replace("/room/"+uuid,"");
        String query = request.getQueryString();
        if (query != null){
            base +="?"+query;
        }
        Map<String,String> returnMap = restTemplate.getForObject("http://localhost:7001/registration/getbyuuid/"+uuid, Map.class);
        //TODO, check of het een api endpoint kan zijn. of dat het misschien een file is. Als het een file is, behandel het als resource
        System.out.println("hier nu dus"+base);
        if (base.contains(".")){
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(new MediaType("*", "*"));
            System.out.println("hier nu dus");
            return new ResponseEntity<byte[]>(getResourceOnURL(returnMap.get("endPoint")+base,uuid),responseHeaders, HttpStatus.OK);
        }
        Object quote = restTemplate.getForObject(returnMap.get("endPoint")+base, Object.class);
        return quote;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/**")
    public Object postAny(HttpServletRequest request,@PathVariable String uuid){
        RestTemplate restTemplate = new RestTemplate();
        String base = request.getServletPath();
        base = base.replace("/room/"+uuid,"");
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
        Map<String,String> returnMap = restTemplate.getForObject("http://localhost:7001/registration/getbyuuid/"+uuid, Map.class);;
        quote = restTemplate.postForEntity(returnMap.get("endPoint")+base, newRequest, Object.class);
        return quote;
    }

}
