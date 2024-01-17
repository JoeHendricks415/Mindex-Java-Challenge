package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingStructureIdUrl;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private EmployeeRepository employeeRepository;


    @Before
    public void setup() {
        reportingStructureIdUrl = "http://localhost:" + port + "/reportingStructure/{id}";
    }

    @Test
    public void testReportingStructureRead() {
        Employee employeeTopManager = new Employee();
        employeeTopManager.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        employeeTopManager.setFirstName("John");
        employeeTopManager.setLastName("Lennon");
        employeeTopManager.setDepartment("Engineering");
        employeeTopManager.setPosition("Tech Manager");

        Employee employeeSubManager = new Employee();
        employeeSubManager.setEmployeeId("03aa1462-ffa9-4978-901b-7c001562cf6f");
        employeeSubManager.setFirstName("Sean");
        employeeSubManager.setLastName("Lennon");
        employeeSubManager.setDepartment("Engineering");
        employeeSubManager.setPosition("Tech Lead");

        Employee firstEmployeeOfSubManger = new Employee();
        firstEmployeeOfSubManger.setEmployeeId("62c1084e-6e34-4630-93fd-9153afb65309");
        firstEmployeeOfSubManger.setFirstName("Ringo");
        firstEmployeeOfSubManger.setLastName("Starr");
        firstEmployeeOfSubManger.setDepartment("Engineering");
        firstEmployeeOfSubManger.setPosition("Developer II");

        Employee secondEmployeeOfSubManger = new Employee();
        secondEmployeeOfSubManger.setEmployeeId("c0c2293d-16bd-4603-8e08-638a9d18b22c");
        secondEmployeeOfSubManger.setFirstName("George");
        secondEmployeeOfSubManger.setLastName("Harrison");
        secondEmployeeOfSubManger.setDepartment("Engineering");
        secondEmployeeOfSubManger.setPosition("Developer I");

        //Establish direct reports for employee's
        List<Employee> employeeTopManagerDirectReports = Arrays.asList(
                employeeSubManager
        );

        employeeTopManager.setDirectReports(employeeTopManagerDirectReports);

        List<Employee> employeeSubManagerDirectReports = Arrays.asList(
                firstEmployeeOfSubManger,
                secondEmployeeOfSubManger
        );

        employeeSubManager.setDirectReports(employeeSubManagerDirectReports);

        //Mock repository
        when(employeeRepository.findByEmployeeId(employeeTopManager.getEmployeeId())).thenReturn(employeeTopManager);
        when(employeeRepository.findByEmployeeId(employeeSubManager.getEmployeeId())).thenReturn(employeeSubManager);
        when(employeeRepository.findByEmployeeId(firstEmployeeOfSubManger.getEmployeeId())).thenReturn(firstEmployeeOfSubManger);
        when(employeeRepository.findByEmployeeId((secondEmployeeOfSubManger.getEmployeeId()))).thenReturn(secondEmployeeOfSubManger);

        //Read checks
        ReportingStructure testReportingStructure = new ReportingStructure(employeeTopManager, 3);
        ReportingStructure createdReportingStructure = restTemplate.getForEntity(reportingStructureIdUrl, ReportingStructure.class, testReportingStructure.getEmployee().getEmployeeId()).getBody();
        
        assertNotNull(createdReportingStructure.getEmployee().getEmployeeId());
        assertReportingStructureEquivalence(testReportingStructure, createdReportingStructure);
    }

    private static void assertReportingStructureEquivalence(ReportingStructure expected, ReportingStructure actual){
        assertEquals(expected.getNumberOfReports(), actual.getNumberOfReports());
        assertEquals(expected.getEmployee().getEmployeeId(), actual.getEmployee().getEmployeeId());
        assertEquals(expected.getEmployee().getPosition(), actual.getEmployee().getPosition());
    }
}
