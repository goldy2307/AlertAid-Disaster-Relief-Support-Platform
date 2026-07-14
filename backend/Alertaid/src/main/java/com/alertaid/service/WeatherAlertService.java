package com.alertaid.service;

import com.alertaid.model.WeatherAlert;
import com.alertaid.repository.WeatherAlertRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherAlertService {

    private final WeatherAlertRepository weatherAlertRepository;
    private final SseService sseService;

    public WeatherAlertService(WeatherAlertRepository weatherAlertRepository, SseService sseService) {
        this.weatherAlertRepository = weatherAlertRepository;
        this.sseService = sseService;
    }

    public List<WeatherAlert> getAllAlerts() {
        return weatherAlertRepository.findAll();
    }

    public Optional<WeatherAlert> getAlertById(Long id) {
        return weatherAlertRepository.findById(id);
    }

    public WeatherAlert createAlert(WeatherAlert alert) {
        alert.setIssuedAt(LocalDateTime.now());
        WeatherAlert saved = weatherAlertRepository.save(alert);
        // Broadcast to subscribers that alerts have been updated
        sseService.broadcast("alerts.all", "alerts-updated", saved.getId());
        return saved;
    }

    public void deleteAlert(Long id) {
        weatherAlertRepository.deleteById(id);
    }

    public List<WeatherAlert> getRecentAlerts() {
        // For now, return all alerts. Can be enhanced with date filtering
        return weatherAlertRepository.findAll();
    }
}