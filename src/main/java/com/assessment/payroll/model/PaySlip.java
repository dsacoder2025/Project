package com.assessment.payroll.model;

import java.math.BigDecimal;
import java.util.Map;

public class PaySlip {
    private Employee employee;
    private BigDecimal grossPay;
    private BigDecimal taxAmount;
    private Map<String, BigDecimal> deductions;
    private BigDecimal netPay;

    public PaySlip(Employee employee, BigDecimal grossPay, BigDecimal taxAmount, Map<String, BigDecimal> deductions, BigDecimal netPay) {
        this.employee = employee;
        this.grossPay = grossPay;
        this.taxAmount = taxAmount;
        this.deductions = deductions;
        this.netPay = netPay;
    }

    public Employee getEmployee() { return employee; }
    public BigDecimal getGrossPay() { return grossPay; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public Map<String, BigDecimal> getDeductions() { return deductions; }
    public BigDecimal getNetPay() { return netPay; }

    @Override
    public String toString() {
        return String.format("PaySlip for %s (ID: %s)\nGross Pay: $%.2f\nTax: $%.2f\nDeductions: %s\nNet Pay: $%.2f",
                employee.getName(), employee.getId(), grossPay, taxAmount, deductions, netPay);
    }
}
