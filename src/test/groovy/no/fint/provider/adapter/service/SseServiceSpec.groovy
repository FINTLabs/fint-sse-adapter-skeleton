package no.fint.provider.adapter.service

import no.fint.provider.customcode.service.EventHandlerService
import spock.lang.Specification

class SseServiceSpec extends Specification {
    private SseService sseService

    void setup() {
        sseService = new SseService(eventHandler: Mock(EventHandlerService), sseEndpoint: "http://localhost")
    }

    def "Register organisation"() {
        when:
        def eventSource = sseService.registerOrg("rogfk.no")

        then:
        eventSource != null
    }

    def "Create WebTarget with http headers"() {
        when:
        def target = sseService.getWebTarget("rogfk.no")

        then:
        target.getUri().toString() == "http://localhost"
        target.getConfiguration().getInstances().size() == 1
    }
}
