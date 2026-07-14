package com.alertaid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private Environment env;

    @GetMapping("/mail")
    public String debugMail() {
        return "USER=" + env.getProperty("MAIL_USERNAME")
               + " | PASS=" + env.getProperty("MAIL_PASSWORD");
    }
}
