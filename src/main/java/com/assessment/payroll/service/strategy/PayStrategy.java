package com.assessment.payroll.service.strategy;

import com.assessment.payroll.model.Employee;
import java.math.BigDecimal;
import java.util.Map;

public interface PayStrategy {
    BigDecimal calculateGrossPay(Employee employee, BigDecimal workUnits);
    Map<String, BigDecimal> calculateDeductions(Employee employee, BigDecimal grossPay);
}
