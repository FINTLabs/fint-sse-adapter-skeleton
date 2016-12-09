package no.fint.provider.hrapp.factory

import spock.lang.Specification

class EmployeeMockFactorySpec extends Specification {
    private EmployeeMockFactory employeeMockFactory

    void setup() {
        employeeMockFactory = new EmployeeMockFactory()
    }

    def "Create employee mock data"() {
        when:
        def employees = employeeMockFactory.getEmployeeMocks("rogfk.no")

        then:
        employees.size() > 0
    }
}
