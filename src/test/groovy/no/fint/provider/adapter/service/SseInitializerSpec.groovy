package no.fint.provider.adapter.service

import org.glassfish.jersey.media.sse.EventSource
import spock.lang.Specification

class SseInitializerSpec extends Specification {
    private SseInitializer sseInitializer
    private String[] organizations
    private SseService sseService

    void setup() {
        organizations = ["rogfk.no", "hfk.no", "vaf.no"]
        sseService = Mock(SseService)
        sseInitializer = new SseInitializer(organizations: organizations, sseService: sseService)
    }

    def "Register and close SSE client for organizations"() {
        given:
        def eventSource = Mock(EventSource)

        when:
        sseInitializer.init()
        def eventSources = sseInitializer.getEventSources()
        sseInitializer.cleanup()

        then:
        3 * sseService.registerOrg(_ as String) >> eventSource
        eventSources.size() == 3
        3 * eventSource.close()
    }
}
