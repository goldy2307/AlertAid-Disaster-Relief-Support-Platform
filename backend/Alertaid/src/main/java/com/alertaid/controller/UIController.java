package com.alertaid.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping("/")
    public String home() {
        // index.html is now the React SPA build output (see frontend/react-app).
        return "forward:/index.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        // Forward to a dashboard landing if present; fallback to index
        return "forward:/index.html";
    }
}
