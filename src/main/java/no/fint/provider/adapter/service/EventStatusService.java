package no.fint.provider.adapter.service;

import jersey.repackaged.com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.provider.adapter.sse.FintHeaders;
import no.fint.provider.customcode.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class EventStatusService {

    @Value("${fint.provider.adapter.status-endpoint}")
    private String statusEndpoint;

    @Autowired
    private RestTemplate restTemplate;

    public Event verifyEvent(Event event) {
        List<String> actionList = Action.getActions();
        if (actionList.contains(event.getAction())) {
            event.setStatus(Status.PROVIDER_ACCEPTED);
        } else {
            event.setStatus(Status.PROVIDER_REJECTED);
        }

        postStatus(event);
        return event;
    }

    private void postStatus(Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.put(FintHeaders.ORG_ID_HEADER, Lists.newArrayList(event.getOrgId()));
        HttpEntity<Object> entity = new HttpEntity<>(event, headers);
        ResponseEntity<Void> response = restTemplate.exchange(statusEndpoint, HttpMethod.POST, entity, Void.class);
        log.info("Provider POST status response: {}", response.getStatusCode());
    }
}
