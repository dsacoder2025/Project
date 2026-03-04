package com.assessment.payroll.controller;

import com.assessment.payroll.model.Employee;
import com.assessment.payroll.model.PaySlip;
import com.assessment.payroll.repository.EmployeeRepository;
import com.assessment.payroll.service.PayrollProcessor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PayrollController {

    private final EmployeeRepository employeeRepository;
    private final PayrollProcessor payrollProcessor;

    public PayrollController(EmployeeRepository employeeRepository, PayrollProcessor payrollProcessor) {
        this.employeeRepository = employeeRepository;
        this.payrollProcessor = payrollProcessor;
    }

    public void addEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public List<PaySlip> processPayroll(Map<String, BigDecimal> employeeIdWorkUnits) {
        Map<Employee, BigDecimal> employeeWorkUnits = employeeIdWorkUnits.entrySet().stream()
                .filter(entry -> employeeRepository.findById(entry.getKey()).isPresent())
                .collect(Collectors.toMap(
                        entry -> employeeRepository.findById(entry.getKey()).get(),
                        Map.Entry::getValue));

        return payrollProcessor.processMonthlyPayroll(employeeWorkUnits);
    }
}
