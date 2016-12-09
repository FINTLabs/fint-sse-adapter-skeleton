package no.fint.provider.adapter.service;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.media.sse.EventSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SseInitializer {

    private Map<String, EventSource> eventSources = new HashMap<>();

    @Value("${fint.provider.adapter.organizations}")
    private String[] organizations;

    @Autowired
    private SseService sseService;

    @PostConstruct
    public void init() {
        Arrays.asList(organizations).forEach(orgId -> eventSources.put(orgId, sseService.registerOrg(orgId)));
    }

    @PreDestroy
    public void cleanup() {
        Arrays.asList(organizations).forEach(orgId -> {
            if (eventSources.get(orgId) != null) {
                log.info("Closing SSE client for: {}", orgId);
                eventSources.get(orgId).close();
            }
        });
    }

    Map<String, EventSource> getEventSources() {
        return eventSources;
    }
}
