package no.fint.provider.adapter.sse;

import no.fint.event.model.Event;

public interface EventHandler {
    void handleEvent(Event event);
    void handleHealthCheck(Event event);
}
