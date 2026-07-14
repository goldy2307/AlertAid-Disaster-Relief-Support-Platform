package com.alertaid.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconController {

    private static final String SVG_LOGO_PATH = "static/assets/alertaid-logo.svg";
    private static final String PNG_LOGO_PATH = "static/assets/alertaid-logo.png";

    @GetMapping(value = "/favicon.ico")
    public ResponseEntity<Resource> favicon() {
        try {
            Resource svg = new ClassPathResource(SVG_LOGO_PATH);
            if (svg.exists()) {
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.parseMediaType("image/svg+xml"))
                        .body(svg);
            }
            Resource png = new ClassPathResource(PNG_LOGO_PATH);
            if (png.exists()) {
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(png);
            }
        } catch (Exception ignored) {
        }
        return ResponseEntity.notFound().build();
    }
}
