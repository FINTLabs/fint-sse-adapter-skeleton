package no.fint.provider.hrapp.service

import no.fint.provider.hrapp.factory.EmployeeMockFactory
import spock.lang.Specification

class EmployeeServiceSpec extends Specification {
    private EmployeeService employeeService
    private EmployeeMockFactory employeeMockFactory

    void setup() {
        employeeMockFactory = new EmployeeMockFactory()
        employeeService = new EmployeeService(employeeMockFactory: employeeMockFactory, organizations: ["rogfk.no", "hfk.no"])
    }

    def "Populate organisation employee list on init"() {
        when:
        employeeService.init()

        then:
        def orgEmployees = employeeService.getOrgEmployees()
        orgEmployees.size() == 2
        orgEmployees.keySet().contains("rogfk.no")
        orgEmployees.keySet().contains("hfk.no")
    }
}
