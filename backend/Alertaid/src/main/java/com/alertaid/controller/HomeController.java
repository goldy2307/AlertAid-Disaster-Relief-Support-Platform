package com.alertaid.controller;  // use your package

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/hello")
    public String sayHello() {
        return "✅ Spring Boot is working with MySQL!";
    }

}
