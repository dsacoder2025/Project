package com.assessment.payroll.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

        @Test
        void testToBuilderAndModification() {
                // Given an original employee
                Employee originalEmployee = Employee.builder()
                                .id("E001")
                                .name("John Doe")
                                .fullTime()
                                .payRate(50.0)
                                .withUnion()
                                .withRetirement()
                                .build();

                // When updating the employee's state using toBuilder
                Employee updatedEmployee = originalEmployee.toBuilder()
                                .name("Johnny Doe")
                                .payRate(60.0)
                                .isUnionMember(false)
                                .partTime()
                                .build();

                // Then verify the specific individual values
                assertAll(
                                () -> assertEquals("E001", updatedEmployee.getId(), "The ID should remain the same"),
                                () -> assertEquals("Johnny Doe", updatedEmployee.getName(),
                                                "The name should be updated"),
                                () -> assertEquals(EmployeeType.PART_TIME, updatedEmployee.getEmployeeType(),
                                                "The employee type should be updated"),
                                () -> assertEquals(0, new BigDecimal("60.0").compareTo(updatedEmployee.getPayRate()),
                                                "The pay rate should be updated"),
                                () -> assertFalse(updatedEmployee.isUnionMember(),
                                                "The union membership should be updated"),
                                () -> assertTrue(updatedEmployee.hasRetirement(),
                                                "The retirement status should remain the same"));

                // Verification that the original untouched items maintain state
                assertAll(
                                () -> assertEquals("John Doe", originalEmployee.getName(),
                                                "The original name should remain intact"),
                                () -> assertEquals(0, new BigDecimal("50.0").compareTo(originalEmployee.getPayRate()),
                                                "Original pay rate must remain unchanged"),
                                () -> assertTrue(originalEmployee.isUnionMember(),
                                                "The original object must remain unaffected"));
        }

        @Test
        void testBuilderThrowsOnMissingName() {
                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                        Employee.builder().id("E123").fullTime().payRate(100.0).build();
                });
                assertTrue(exception.getMessage().contains("Name is required"));
        }

        @Test
        void testBuilderThrowsOnMissingId() {
                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                        Employee.builder().name("Test").fullTime().payRate(100.0).build();
                });
                assertTrue(exception.getMessage().contains("ID is required"));
        }

        @Test
        void testBuilderThrowsOnNegativePayRate() {
                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                        Employee.builder().id("E1").name("Test").fullTime().payRate(-50.0).build();
                });
                assertTrue(exception.getMessage().contains("Pay Rate cannot be negative"));
        }
}
