package com.assessment.payroll.service;

import com.assessment.payroll.model.Employee;
import com.assessment.payroll.model.EmployeeType;
import com.assessment.payroll.model.PaySlip;
import com.assessment.payroll.service.strategy.PayStrategy;
import com.assessment.payroll.service.strategy.TaxStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PayrollProcessor {

    private final Map<EmployeeType, PayStrategy> strategies;
    private final TaxStrategy taxStrategy;

    public PayrollProcessor(Map<EmployeeType, PayStrategy> strategies, TaxStrategy taxStrategy) {
        if (strategies == null || taxStrategy == null) {
            throw new IllegalArgumentException("Strategies and TaxStrategy cannot be null");
        }
        this.strategies = strategies;
        this.taxStrategy = taxStrategy;
    }

    private PayStrategy getStrategy(EmployeeType type) {
        PayStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for employee type: " + type);
        }
        return strategy;
    }

    public BigDecimal calculateGrossPay(Employee employee, BigDecimal hoursOrDays) {
        if (employee == null)
            throw new IllegalArgumentException("Employee cannot be null");
        return round(getStrategy(employee.getEmployeeType()).calculateGrossPay(employee, hoursOrDays));
    }

    public BigDecimal calculateTax(BigDecimal grossPay) {
        return taxStrategy.calculateTax(grossPay);
    }

    public Map<String, BigDecimal> calculateDeductions(Employee employee, BigDecimal grossPay) {
        if (employee == null)
            throw new IllegalArgumentException("Employee cannot be null");
        Map<String, BigDecimal> rawDeductions = getStrategy(employee.getEmployeeType()).calculateDeductions(employee,
                grossPay);

        Map<String, BigDecimal> roundedDeductions = new HashMap<>();
        for (Map.Entry<String, BigDecimal> entry : rawDeductions.entrySet()) {
            roundedDeductions.put(entry.getKey(), round(entry.getValue()));
        }
        return roundedDeductions;
    }

    public PaySlip generatePaySlip(Employee employee, BigDecimal hoursOrDays) {
        BigDecimal grossPay = calculateGrossPay(employee, hoursOrDays);
        if (grossPay.compareTo(BigDecimal.ZERO) < 0)
            grossPay = BigDecimal.ZERO;

        BigDecimal tax = calculateTax(grossPay);
        Map<String, BigDecimal> deductions = calculateDeductions(employee, grossPay);

        BigDecimal totalDeductions = deductions.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal netPay = round(grossPay.subtract(tax).subtract(totalDeductions));

        // Prevent negative net pay
        if (netPay.compareTo(BigDecimal.ZERO) < 0) {
            System.err.println("WARNING: Target Deductions (" + totalDeductions + ") exceeded absolute Gross Pay ("
                    + grossPay + ") for Employee: " + employee.getName()
                    + ". Net Pay adjusted successfully to 0.00 limits.");
            netPay = BigDecimal.ZERO;
        }

        return new PaySlip(employee, grossPay, tax, deductions, netPay);
    }

    public List<PaySlip> processMonthlyPayroll(Map<Employee, BigDecimal> employeeWorkUnits) {
        if (employeeWorkUnits == null)
            throw new IllegalArgumentException("Input map cannot be null");

        return employeeWorkUnits.entrySet().stream()
                .map(entry -> generatePaySlip(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
