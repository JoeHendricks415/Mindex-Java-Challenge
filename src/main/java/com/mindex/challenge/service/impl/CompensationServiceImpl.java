package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * This method creates a Compensation if the compensation for employee does not exist. If compensation already
     * exists it will throw a RunTimeException. Requirements do not mention an update API, so this is a preventive
     * measure to avoid Internal Server Errors when creating/reading duplicate compensations.
     */
    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        Employee employee = employeeRepository.findByEmployeeId(compensation.getEmployee().getEmployeeId());

        if (employee == null) {
            throw new RuntimeException("Cannot create compensation report for employee id: " + compensation.getEmployee().getEmployeeId());
        }

        Compensation existingCompensation = compensationRepository.findByEmployee_EmployeeId(compensation.getEmployee().getEmployeeId());

        if(existingCompensation == null) {
            compensation.setEmployee(employee);
            compensationRepository.insert(compensation);
        } else {
            throw new RuntimeException("Compensation report already exists for employee id: "  + compensation.getEmployee().getEmployeeId());
        }

        return compensation;
    }

    @Override
    public Compensation read(String employeeId) {
        LOG.debug("Reading compensation for employee id [{}]", employeeId);

        Compensation compensation = compensationRepository.findByEmployee_EmployeeId(employeeId);

        if (compensation == null) {
            throw new RuntimeException("Compensation not found for employee id: " + employeeId);
        }
        return compensation;
    }
}
