package com.alertaid.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class SseService {

    private final Map<String, Set<SseEmitter>> channels = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String channel) {
        SseEmitter emitter = new SseEmitter(30L * 60L * 1000L); // 30 minutes
        channels.computeIfAbsent(channel, k -> new CopyOnWriteArraySet<>()).add(emitter);
        emitter.onCompletion(() -> remove(channel, emitter));
        emitter.onTimeout(() -> remove(channel, emitter));
        emitter.onError(ex -> remove(channel, emitter));
        return emitter;
    }

    private void remove(String channel, SseEmitter emitter) {
        Set<SseEmitter> set = channels.get(channel);
        if (set != null) {
            set.remove(emitter);
        }
    }

    public void broadcast(String channel, String eventName, Object data) {
        Set<SseEmitter> set = channels.get(channel);
        if (set == null) return;
        for (SseEmitter emitter : set) {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event().name(eventName).data(data);
                emitter.send(event);
            } catch (IOException | IllegalStateException e) {
                // If the connection is already in error/complete state, avoid propagating
                // IllegalStateException back into the request thread. Just clean up.
                try {
                    emitter.complete();
                } catch (IllegalStateException ignored) {
                    // Emitter is already completed or in error; safe to ignore.
                }
                remove(channel, emitter);
            }
        }
    }
}