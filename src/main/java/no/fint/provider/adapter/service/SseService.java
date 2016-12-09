package no.fint.provider.adapter.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.provider.adapter.sse.FintHeaders;
import no.fint.provider.adapter.sse.FintEventListener;
import no.fint.provider.adapter.sse.SseHeaderProvider;
import no.fint.provider.adapter.sse.SseHeaderSupportFeature;
import no.fint.provider.customcode.service.EventHandlerService;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.SseFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class SseService {

    @Autowired
    EventHandlerService eventHandler;

    @Value("${fint.provider.adapter.sse-endpoint}")
    String sseEndpoint;

    @Async
    public EventSource registerOrg(String orgId) {
        WebTarget webTarget = getWebTarget(orgId);

        EventSource eventSource = EventSource.target(webTarget).build();
        FintEventListener fintEventListener = new FintEventListener(eventHandler, orgId);
        eventSource.register(fintEventListener);
        eventSource.open();
        log.info("Starting SSE client for: {}", orgId);

        return eventSource;
    }

    WebTarget getWebTarget(String orgId) {
        Map<String, String> map = new HashMap<>();
        map.put(FintHeaders.ORG_ID_HEADER, orgId);
        SseHeaderProvider provider = () -> map;
        Client client = ClientBuilder.newBuilder()
                .register(SseFeature.class)
                .register(new SseHeaderSupportFeature(provider))
                .build();

        String sseUrl = String.format(sseEndpoint, UUID.randomUUID().toString());
        return client.target(sseUrl);
    }

}
