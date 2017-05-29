package no.fint.provider.customcode.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.event.model.Health;
import no.fint.event.model.Status;
import no.fint.model.relation.FintResource;
import no.fint.provider.adapter.event.EventResponseService;
import no.fint.provider.adapter.event.EventStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventHandlerService {

    @Autowired
    private EventResponseService eventResponseService;

    @Autowired
    private EventStatusService eventStatusService;

    public void handleEvent(String json) {
        Event event = EventUtil.toEvent(json);
        if (event.isHealthCheck()) {
            postHealthCheckResponse(event);
        } else {
            Event<FintResource> responseEvent = new Event<>(event);
            responseEvent.setStatus(Status.PROVIDER_ACCEPTED);
            eventStatusService.postStatus(responseEvent);

            /*
             * Add if statements for all the actions
             */

            responseEvent.setStatus(Status.PROVIDER_RESPONSE);
            eventResponseService.postResponse(responseEvent);
        }
    }

    private void postHealthCheckResponse(Event event) {
        Event<Health> healthCheckEvent = new Event<>(event);
        healthCheckEvent.setStatus(Status.TEMP_UPSTREAM_QUEUE);
        if (healthCheck()) {
            healthCheckEvent.addData(new Health("adapter", "I'm fine thanks! How are you?"));
        } else {
            healthCheckEvent.addData(new Health("adapter", "Oh, I'm feeling bad! How are you?"));
        }

        eventResponseService.postResponse(healthCheckEvent);
    }

    private boolean healthCheck() {
        /*
         * Check application connectivity etc.
         */
        return true;
    }
}
