package com.assessment.payroll.service.strategy;

import java.math.BigDecimal;

public interface TaxStrategy {
    BigDecimal calculateTax(BigDecimal grossPay);
}
