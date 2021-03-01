package com.itdemo.gulimail.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {

    @RequestMapping("/{page}.html")
    public String hello(@PathVariable("page") String page){
        return page;
    }
}
