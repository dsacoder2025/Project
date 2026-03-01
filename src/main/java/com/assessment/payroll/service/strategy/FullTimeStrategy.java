package com.assessment.payroll.service.strategy;

import com.assessment.payroll.model.Employee;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class FullTimeStrategy implements PayStrategy {

    @Override
    public BigDecimal calculateGrossPay(Employee employee, BigDecimal workUnits) {
        
        return employee.getPayRate();
    }

    @Override
    public Map<String, BigDecimal> calculateDeductions(Employee employee, BigDecimal grossPay) {
        Map<String, BigDecimal> deductions = new HashMap<>();
        
        
        deductions.put("Health Insurance", BigDecimal.valueOf(150.00));

        
        if (employee.hasRetirement()) {
             
             deductions.put("Retirement", grossPay.multiply(BigDecimal.valueOf(0.05)));
        }
        
        if (employee.isUnionMember()) {
            deductions.put("Union Dues", BigDecimal.valueOf(50.00));
        }
        
        return deductions;
    }
}
