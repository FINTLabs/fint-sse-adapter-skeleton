package no.fint.adapter.sse;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.provider.adapter.event.EventStatusService;
import no.fint.provider.customcode.service.EventHandlerService;
import no.fint.sse.AbstractEventListener;
import org.glassfish.jersey.media.sse.InboundEvent;

import java.util.List;

/**
 * Event listener for the for the SSE client. When an inbound event is received the {@link #onEvent(InboundEvent)} method
 * calls {@link EventHandlerService} service.
 */
@Slf4j
public class FintEventListener extends AbstractEventListener {

    private final EventStatusService eventStatusService;
    private final List<EventHandler> eventHandlers;

    public FintEventListener(EventStatusService eventStatusService, List<EventHandler> eventHandlers) {
        this.eventStatusService = eventStatusService;
        this.eventHandlers = eventHandlers;
    }

    @Override
    public void onEvent(Event event) {
        log.info("EventListener for {}", event.getOrgId());
        log.info("Processing event: {}, for orgId: {}, for client: {}, action: {}",
                event.getCorrId(),
                event.getOrgId(),
                event.getClient(),
                event.getAction());

        if (event.isHealthCheck()) {
            eventHandlers.forEach(eventHandler -> eventHandler.handleHealthCheck(event));
        } else if (eventStatusService.verifyEvent(event).getStatus() == Status.ADAPTER_ACCEPTED) {
            eventHandlers.forEach(eventHandler -> eventHandler.handleEvent(event));
        } else {
            log.debug("Event rejected: {}", event);
        }
    }
}
