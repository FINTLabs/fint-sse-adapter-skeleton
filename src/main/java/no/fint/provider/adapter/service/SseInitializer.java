package no.fint.provider.adapter.service;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.fint.provider.adapter.sse.FintEventListener;
import no.fint.provider.adapter.sse.FintHeaders;
import no.fint.sse.FintSse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class SseInitializer {

    @Getter
    private List<FintSse> sseClients = new ArrayList<>();

    @Value("${fint.provider.adapter.organizations}")
    private String[] organizations;

    @Value("${fint.provider.adapter.sse-endpoint}")
    private String sseEndpoint;

    @Autowired
    private FintEventListener fintEventListener;

    @PostConstruct
    public void init() {
        Arrays.asList(organizations).forEach(orgId -> {
            FintSse fintSse = new FintSse(sseEndpoint);
            fintSse.connect(fintEventListener, ImmutableMap.of(FintHeaders.HEADER_ORG_ID, orgId));
            sseClients.add(fintSse);
        });
    }

    @PreDestroy
    public void cleanup() {
        for (FintSse sseClient : sseClients) {
            sseClient.close();
        }
    }
}
