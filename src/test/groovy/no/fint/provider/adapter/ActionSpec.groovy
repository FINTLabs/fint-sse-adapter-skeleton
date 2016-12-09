package no.fint.provider.adapter

import no.fint.provider.hrapp.Action
import spock.lang.Specification


class ActionSpec extends Specification {

    def "Get list of strings from enum names"() {
        when:
        List<String> actions = Action.getActions()

        then:
        actions.size() == Action.values().length
    }
}
