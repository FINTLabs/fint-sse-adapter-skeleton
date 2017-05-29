package no.fint.provider.adapter.event

import no.fint.event.model.DefaultActions
import no.fint.event.model.Event
import no.fint.provider.adapter.FintAdapterProps
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class EventStatusServiceSpec extends Specification {
    private EventStatusService eventStatusService
    private FintAdapterProps fintAdapterProps
    private RestTemplate restTemplate

    void setup() {
        restTemplate = Mock(RestTemplate)
        fintAdapterProps = Mock(FintAdapterProps)
        eventStatusService = new EventStatusService(props: fintAdapterProps, restTemplate: restTemplate)
    }

    def "POST event status"() {
        given:
        def event = new Event(orgId: 'rogfk.no', action: DefaultActions.HEALTH.name())

        when:
        eventStatusService.postStatus(event)

        then:
        1 * fintAdapterProps.getStatusEndpoint() >> 'http://localhost'
        1 * restTemplate.exchange(_ as String, _ as HttpMethod, _ as HttpEntity, _ as Class) >> ResponseEntity.ok().build()
    }
}