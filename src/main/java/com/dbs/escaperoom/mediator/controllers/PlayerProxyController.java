package com.dbs.escaperoom.mediator.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping(value = "/player",produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class PlayerProxyController extends ProxyController {
    public PlayerProxyController(){
        this.proxyBase = "/player";
        this.proxyTargetURL = "http://localhost:7003";
    }


}
