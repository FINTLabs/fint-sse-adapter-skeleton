package no.fint.provider.adapter.service

import spock.lang.Specification

class SseInitializerSpec extends Specification {
    private SseInitializer sseInitializer
    private String[] organizations

    void setup() {
        organizations = ['rogfk.no', 'hfk.no', 'vaf.no']
        sseInitializer = new SseInitializer(sseEndpoint: 'http://localhost', organizations: organizations)
    }

    def "Register and close SSE client for organizations"() {
        when:
        sseInitializer.init()

        then:
        sseInitializer.sseClients.size() == 3
    }
}
