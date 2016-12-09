package no.fint.provider.adapter.service;

import jersey.repackaged.com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import no.fint.provider.adapter.sse.FintHeaders;
import no.fint.event.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class EventResponseService {

    @Value("${fint.provider.adapter.response-endpoint}")
    private String responseEndpoint;

    @Autowired
    private RestTemplate restTemplate;

    public void postResponse(Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.put(FintHeaders.ORG_ID_HEADER, Lists.newArrayList(event.getOrgId()));
        ResponseEntity<Void> response = restTemplate.exchange(responseEndpoint, HttpMethod.POST, new HttpEntity<>(event, headers), Void.class);
        log.info("Provider POST  response: {}", response.getStatusCode());
    }
}
