package com.assessment.payroll.service;

import com.assessment.payroll.model.Employee;
import com.assessment.payroll.model.PaySlip;
import com.assessment.payroll.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PayrollService {

    private final EmployeeRepository employeeRepository;
    private final PayrollProcessor payrollProcessor;

    public PayrollService(EmployeeRepository employeeRepository, PayrollProcessor payrollProcessor) {
        this.employeeRepository = employeeRepository;
        this.payrollProcessor = payrollProcessor;
    }

    public void registerEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public Employee getEmployee(String id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<PaySlip> processPayroll(Map<String, BigDecimal> employeeWorkUnits) {
        List<PaySlip> paySlips = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : employeeWorkUnits.entrySet()) {
            Employee employee = getEmployee(entry.getKey());
            BigDecimal workUnits = entry.getValue();

            PaySlip slip = payrollProcessor.generatePaySlip(employee, workUnits);
            paySlips.add(slip);
        }

        return paySlips;
    }
}
