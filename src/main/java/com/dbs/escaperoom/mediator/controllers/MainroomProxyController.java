package com.dbs.escaperoom.mediator.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequestMapping(value = "/mainroom",produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class MainroomProxyController extends ProxyController {
    public MainroomProxyController(){
        this.proxyBase="/mainroom";
        this.proxyTargetURL="http://localhost:7001";
    }
}
