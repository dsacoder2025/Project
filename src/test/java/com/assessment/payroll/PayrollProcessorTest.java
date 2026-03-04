package com.assessment.payroll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.assessment.payroll.model.Employee;
import com.assessment.payroll.model.PaySlip;
import com.assessment.payroll.service.PayrollProcessor;

public class PayrollProcessorTest {

    private PayrollProcessor processor;

    @BeforeEach
    public void setup() {
        processor = new PayrollProcessor();
    }

    @Test
    public void testFullTimeEmployee() {
        // Fixed monthly salary 6000
        Employee emp = Employee.builder()
                .id("1").name("Alice").fullTime().payRate(6000.0)
                .build();

        BigDecimal gross = processor.calculateGrossPay(emp, BigDecimal.ZERO); // hours ignored
        assertEquals(0, BigDecimal.valueOf(6000.0).compareTo(gross));

        // Tax: 900
        assertEquals(0, BigDecimal.valueOf(900.0).compareTo(processor.calculateTax(gross)));

        // Deductions: Health (150)
        Map<String, BigDecimal> deductions = processor.calculateDeductions(emp, gross);
        assertEquals(0, BigDecimal.valueOf(150.0).compareTo(deductions.get("Health Insurance")));

        PaySlip slip = processor.generatePaySlip(emp, BigDecimal.ZERO);
        assertEquals(0, BigDecimal.valueOf(6000 - 900 - 150).compareTo(slip.getNetPay()));
    }

    @Test
    public void testPartTimeEmployeeCap() {
        // Hourly 20, 150 hours -> caps at 120
        Employee emp = Employee.builder()
                .id("2").name("Bob").partTime().payRate(20.0)
                .build();

        BigDecimal gross = processor.calculateGrossPay(emp, BigDecimal.valueOf(150));
        assertEquals(0, BigDecimal.valueOf(2400.0).compareTo(gross)); // 120 * 20
    }

    @Test
    public void testContractor() {
        // Daily 200, 10 days
        Employee emp = Employee.builder()
                .id("3").name("Charlie").contractor().payRate(200.0)
                .withUnion().withRetirement()
                .build();

        BigDecimal gross = processor.calculateGrossPay(emp, BigDecimal.valueOf(10));
        assertEquals(0, BigDecimal.valueOf(2000.0).compareTo(gross));

        // Deductions: Union (50), Retirement (5% of 2000 = 100)
        Map<String, BigDecimal> deductions = processor.calculateDeductions(emp, gross);
        assertEquals(0, BigDecimal.valueOf(50.0).compareTo(deductions.get("Union Dues")));
        assertEquals(0, BigDecimal.valueOf(100.0).compareTo(deductions.get("Retirement")));
        assertNull(deductions.get("Health Insurance")); // FT only
    }

    @ParameterizedTest
    @CsvSource({
            "500, 0.0",
            "1000, 0.0",
            "2000, 100.0", // (2000-1000)*0.1 = 100
            "3000, 200.0", // (3000-1000)*0.1 = 200
            "4000, 400.0", // 200 + (4000-3000)*0.2 = 200+200=400
            "5000, 600.0", // 200 + (5000-3000)*0.2 = 600
            "6000, 900.0" // 600 + (6000-5000)*0.3 = 900
    })
    public void testTaxBrackets(double gross, double expectedTax) {
        BigDecimal grossBD = BigDecimal.valueOf(gross);
        BigDecimal expectedBD = BigDecimal.valueOf(expectedTax);
        assertEquals(0, expectedBD.compareTo(processor.calculateTax(grossBD)));
    }

    @Test
    public void testDeductionsExceedingGrossPay() {
        // Employee with 0 hours, but has union and health deductions
        Employee emp = Employee.builder()
                .id("4").name("David").fullTime().payRate(0.0) // 0 pay rate
                .withUnion()
                .build();

        PaySlip slip = processor.generatePaySlip(emp, BigDecimal.ZERO);

        // Gross is 0, Tax is 0. Deductions are Health (150) + Union (50) = 200.
        // Net pay should be safely zeroed out, not negative.
        assertEquals(0, BigDecimal.ZERO.compareTo(slip.getGrossPay()));
        assertEquals(0, BigDecimal.ZERO.compareTo(slip.getNetPay()));
    }

    @Test
    public void testNullEmployeeThrowsException() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            processor.calculateGrossPay(null, BigDecimal.TEN);
        });
    }

    @Test
    public void testNegativeWorkUnitsThrowsException() {
        Employee emp = Employee.builder()
                .id("5").name("Eve").partTime().payRate(20.0).build();

        // Passing negative hours shouldn't result in negative pay, it should be
        // rejected.
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            processor.calculateGrossPay(emp, BigDecimal.valueOf(-10));
        });
    }

    @Test
    public void testExtremeLargeSalaryPrecision() {
        // Testing BigDecimal limits with billions to ensure it safely scales without
        // floating point anomalies
        Employee emp = Employee.builder()
                .id("6").name("CEO").fullTime().payRate(999999999.99).withUnion().build(); // Just under a Billion

        BigDecimal gross = processor.calculateGrossPay(emp, BigDecimal.ZERO);
        // Tax is base 600 + (Gross - 5000) * 0.3
        BigDecimal expectedTax = BigDecimal.valueOf(600).add(
                gross.subtract(BigDecimal.valueOf(5000)).multiply(BigDecimal.valueOf(0.3)))
                .setScale(2, java.math.RoundingMode.HALF_UP);

        assertEquals(0, expectedTax.compareTo(processor.calculateTax(gross)));

        PaySlip slip = processor.generatePaySlip(emp, BigDecimal.ZERO);

        BigDecimal expectedNet = gross.subtract(expectedTax).subtract(BigDecimal.valueOf(200)) // Union(50) +
                                                                                               // Health(150)
                .setScale(2, java.math.RoundingMode.HALF_UP);
        assertEquals(0, expectedNet.compareTo(slip.getNetPay()));
    }
}
