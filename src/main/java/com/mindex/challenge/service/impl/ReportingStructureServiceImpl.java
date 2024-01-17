package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;


    public ReportingStructure read(String id){
        LOG.debug("Creating reporting structure for employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Employee not found for employee id: " + id);
        }

        int directReportCount = this.getDirectReportCount(employee);
        ReportingStructure reportingStructure = new ReportingStructure(employee, directReportCount);

        return reportingStructure;
    }

     /**
     * Method returns total number of reports when passing an employee, including all of their distinct reports.
     * This method will repeat recursively until all distinct reports, if any, are found.
     **/
     private int getDirectReportCount(Employee employee){
        int directReportCount = 0;

        if(employee.getDirectReports() != null) {
            for (Employee emp : employee.getDirectReports()) {
                directReportCount += 1;
                directReportCount += getDirectReportCount(emp);
            }
        }
        return directReportCount;
    }

    /** Not used, but alternate version of getDirectReportCount using Streams **/
    private int getDirectReportCountUsingStreams(Employee employee) {
        return Optional.ofNullable(employee)
                .map(Employee::getDirectReports)
                .map(directReports -> directReports.stream().mapToInt(emp -> 1 + getDirectReportCount(emp)).sum())
                .orElse(0);
    }
}
