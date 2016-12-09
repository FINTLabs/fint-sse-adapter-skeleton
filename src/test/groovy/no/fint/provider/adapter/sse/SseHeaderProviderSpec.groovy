package no.fint.provider.adapter.sse

import spock.lang.Specification

class SseHeaderProviderSpec extends Specification {

    def "Initialize sse header provider"() {

        when:

        SseHeaderProvider provider = new SseHeaderProvider() {
            @Override
            Map<String, String> getHeaders() {
                Map<String, String> map = new HashMap<>()
                map.put("test", "test")
                return map
            }
        }

        then:
        provider.headers.values().size() == 1
        provider.headers.get("test") == "test"

    }

}
