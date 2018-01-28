package no.fint.provider.adapter

import spock.lang.Specification

class FintAdapterPropsSpec extends Specification {
    private FintAdapterProps props

    void setup() {
        props = new FintAdapterProps(baseUrl: 'http://localhost')
    }

    def "Get sse endpoint"() {
        when:
        def sseEndpoint = props.getSseEndpoint()

        then:
        sseEndpoint == 'http://localhost/sse/%s'
    }

    def "Get response endpoint"() {
        when:
        def responseEndpoint = props.getResponseEndpoint()

        then:
        responseEndpoint == 'http://localhost/response'
    }

    def "Get status endpoint"() {
        when:
        def statusEndpoint = props.getStatusEndpoint()

        then:
        statusEndpoint == 'http://localhost/status'
    }

}
