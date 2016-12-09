package no.fint.provider.adapter.sse;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.provider.customcode.service.EventHandlerService;
import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.InboundEvent;

@Slf4j
public class FintEventListener implements EventListener {

    private String orgId;
    private EventHandlerService eventHandler;

    public FintEventListener(EventHandlerService eventHandler, String orgId) {
        this.orgId = orgId;
        this.eventHandler = eventHandler;
    }

    @Override
    public void onEvent(InboundEvent inboundEvent) {
        if (inboundEvent.getName().equals("event")) {
            String jsonEvent = inboundEvent.readData(String.class);
            Event<?> event = EventUtil.toEvent(jsonEvent);

            if(event == null) {
                log.error("Could not parse Event object");
            } else {
                if (event.getOrgId() != null && event.getOrgId().equals(orgId)) {
                    log.info("EventListener for {}", event.getOrgId());
                    log.info("Processing event: {}, for orgId: {}, for client: {}, action: {}",
                            event.getCorrId(),
                            event.getOrgId(),
                            event.getClient(),
                            event.getAction());

                    eventHandler.handleEvent(jsonEvent);
                } else {
                    log.info("This is not EventListener for {}", event.getOrgId());
                }
            }

        }

    }
}
