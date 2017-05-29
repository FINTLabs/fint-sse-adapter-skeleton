package no.fint.provider.customcode.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.fint.event.model.Event
import no.fint.provider.adapter.event.EventResponseService
import no.fint.provider.adapter.event.EventStatusService
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

    def "Post response on health check"() {
        given:
        def event = new Event('rogfk.no', 'test', 'GET_ALL', 'test')
        def json = new ObjectMapper().writeValueAsString(event)

        when:
        eventHandlerService.handleEvent(json)

        then:
        1 * eventResponseService.postResponse(_ as Event)
    }
}
