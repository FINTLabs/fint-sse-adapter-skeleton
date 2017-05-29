package no.fint.provider.adapter.event

import no.fint.provider.adapter.FintAdapterProps
import no.fint.provider.adapter.sse.SseInitializer
import spock.lang.Specification

class SseInitializerSpec extends Specification {
    private SseInitializer sseInitializer
    private FintAdapterProps props

    void setup() {
        props = Mock(FintAdapterProps) {
            getOrganizations() >> ['rogfk.no', 'hfk.no', 'vaf.no']
            getSseEndpoint() >> 'http://localhost'
        }
        sseInitializer = new SseInitializer(props: props)
    }

    def "Register and close SSE client for organizations"() {
        when:
        sseInitializer.init()

        then:
        sseInitializer.sseClients.size() == 3
    }
}
