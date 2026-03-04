package com.assessment.payroll.service.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DefaultTaxStrategy implements TaxStrategy {

    @Override
    public BigDecimal calculateTax(BigDecimal grossPay) {
        if (grossPay.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal tax = BigDecimal.ZERO;

        BigDecimal threshold1 = BigDecimal.valueOf(1000);
        BigDecimal threshold2 = BigDecimal.valueOf(3000);
        BigDecimal threshold3 = BigDecimal.valueOf(5000);

        if (grossPay.compareTo(threshold1) <= 0) {
            tax = BigDecimal.ZERO;
        } else if (grossPay.compareTo(threshold2) <= 0) {
            tax = grossPay.subtract(threshold1).multiply(BigDecimal.valueOf(0.10));
        } else if (grossPay.compareTo(threshold3) <= 0) {
            BigDecimal baseTax = BigDecimal.valueOf(200);
            tax = baseTax.add(grossPay.subtract(threshold2).multiply(BigDecimal.valueOf(0.20)));
        } else {
            BigDecimal baseTax = BigDecimal.valueOf(600);
            tax = baseTax.add(grossPay.subtract(threshold3).multiply(BigDecimal.valueOf(0.30)));
        }

        return tax.setScale(2, RoundingMode.HALF_UP);
    }
}
