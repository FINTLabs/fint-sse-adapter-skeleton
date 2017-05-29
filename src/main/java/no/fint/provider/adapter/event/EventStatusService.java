package no.fint.provider.adapter.event;

import jersey.repackaged.com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.provider.adapter.FintAdapterProps;
import no.fint.provider.adapter.sse.FintHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class EventStatusService {

    @Autowired
    private FintAdapterProps props;

    @Autowired
    private RestTemplate restTemplate;

    public void postStatus(Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.put(FintHeaders.HEADER_ORG_ID, Lists.newArrayList(event.getOrgId()));
        ResponseEntity<Void> response = restTemplate.exchange(props.getStatusEndpoint(), HttpMethod.POST, new HttpEntity<>(event, headers), Void.class);
        log.info("Provider POST status response: {}", response.getStatusCode());
    }
}
