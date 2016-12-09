package no.fint.provider.adapter.service

import no.fint.event.model.Event
import no.fint.event.model.Status
import no.fint.provider.customcode.Action
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class EventStatusServiceSpec extends Specification {
    private EventStatusService eventStatusService
    private RestTemplate restTemplate

    void setup() {
        restTemplate = Mock(RestTemplate)
        eventStatusService = new EventStatusService(statusEndpoint: "http://localhost", restTemplate: restTemplate)
    }

    def "Verify valid event"() {
        given:
        def event = new Event(orgId: "rogfk.no", action: Action.HEALTH)

        when:
        def response = eventStatusService.verifyEvent(event)

        then:
        1 * restTemplate.exchange(_ as String, _ as HttpMethod, _ as HttpEntity, _ as Class) >> ResponseEntity.ok().build()
        response.status == Status.PROVIDER_ACCEPTED
    }

    def "Verify invalid event"() {
        given:
        def event = new Event(orgId: "rogfk.no", action: "testing")

        when:
        def response = eventStatusService.verifyEvent(event)

        then:
        1 * restTemplate.exchange(_ as String, _ as HttpMethod, _ as HttpEntity, _ as Class) >> ResponseEntity.ok().build()
        response.status == Status.PROVIDER_REJECTED
    }
}
