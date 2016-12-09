package no.fint.provider.customcode.service

import no.fint.event.model.Event
import no.fint.event.model.Status
import no.fint.provider.adapter.service.EventResponseService
import no.fint.provider.adapter.service.EventStatusService
import spock.lang.Specification

class EventHandlerServiceSpec extends Specification {
    private EventHandlerService eventHandlerService
    private EventStatusService eventStatusService
    private EventResponseService eventResponseService

    void setup() {
        eventStatusService = Mock(EventStatusService)
        eventResponseService = Mock(EventResponseService)
        eventHandlerService = new EventHandlerService(eventStatusService: eventStatusService, eventResponseService: eventResponseService)
    }

    def "Do nothing when event status is not PROVIDER_ACCEPTED"() {
        given:
        def json = "{\"corrId\":\"c978c986-8d50-496f-8afd-8d27bd68049b\",\"action\":\"action\",\"status\":\"NEW\",\"time\":1481116509260,\"orgId\":\"rogfk.no\",\"source\":\"source\",\"client\":\"client\",\"message\":null,\"data\":[]}"

        when:
        eventHandlerService.handleEvent(json)

        then:
        1 * eventStatusService.verifyEvent(_ as Event) >> new Event()
        0 * eventResponseService.postResponse(_ as Event)
    }

    def "Post response on health check"() {
        given:
        def json = "{\"corrId\":\"c978c986-8d50-496f-8afd-8d27bd68049b\",\"action\":\"HEALTH\",\"status\":\"PROVIDER_ACCEPTED\",\"time\":1481116509260,\"orgId\":\"rogfk.no\",\"source\":\"source\",\"client\":\"client\",\"message\":null,\"data\":[]}"
        def event = new Event()
        event.setStatus(Status.PROVIDER_ACCEPTED)

        when:
        eventHandlerService.handleEvent(json)

        then:
        1 * eventStatusService.verifyEvent(_ as Event) >> event
        1 * eventResponseService.postResponse(_ as Event)
    }
}
