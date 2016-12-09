package no.fint.provider.adapter.sse

import spock.lang.Specification

import javax.ws.rs.client.ClientRequestContext
import javax.ws.rs.core.MultivaluedMap

class SseHeaderSupportFilterSpec extends Specification {
    private SseHeaderSupportFilter sseHeaderSupportFilter
    private SseHeaderProvider sseHeaderProvider
    private ClientRequestContext clientRequestContext

    void setup() {
        clientRequestContext = Mock(ClientRequestContext)
        sseHeaderProvider = Mock(SseHeaderProvider) {
            getHeaders() >> ["x-org-id":"rogfk.no"]
        }
        sseHeaderSupportFilter = new SseHeaderSupportFilter(sseHeaderProvider)
    }

    def "Filter SSE request"() {
        when:
        sseHeaderSupportFilter.filter(clientRequestContext)

        then:
        1 * clientRequestContext.getHeaders() >> Mock(MultivaluedMap)
    }
}
