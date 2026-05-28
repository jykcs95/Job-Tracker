package com.tracker.analytics_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseConnectionManager {

    private static final Logger log = LoggerFactory.getLogger(SseConnectionManager.class);

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter createConnection() {
        SseEmitter emitter = new SseEmitter(1800000L);
        this.emitters.add(emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));
        emitter.onError(ex -> this.emitters.remove(emitter));

        log.info("📡 New active UI streaming channel connected. Active pools: {}", emitters.size());
        return emitter;
    }

    public void broadcastUpdate(Object data) {
        log.info("⚡ Broadcasting live metric event payload to {} connected browser windows...", emitters.size());
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("telemetry-update").data(data));
            } catch (IOException _) {
                this.emitters.remove(emitter);
            }
        }
    }
}
