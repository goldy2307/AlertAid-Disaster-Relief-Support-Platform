package com.alertaid.controller;

import com.alertaid.model.WeatherAlert;
import com.alertaid.service.WeatherAlertService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather-alerts")
public class WeatherAlertController {

    private final WeatherAlertService weatherAlertService;

    public WeatherAlertController(WeatherAlertService weatherAlertService) {
        this.weatherAlertService = weatherAlertService;
    }

    @PostMapping
    public WeatherAlert createAlert(@RequestBody WeatherAlert weatherAlert) {
        return weatherAlertService.createAlert(weatherAlert);
    }

    @GetMapping
    public List<WeatherAlert> getAllAlerts() {
        return weatherAlertService.getAllAlerts();
    }

    @GetMapping("/{id}")
    public WeatherAlert getAlertById(@PathVariable Long id) {
        return weatherAlertService.getAlertById(id).orElse(null);
    }
}
