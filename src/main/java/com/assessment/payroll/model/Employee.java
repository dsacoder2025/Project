package com.assessment.payroll.model;

import java.math.BigDecimal;

public class Employee {
    private String id;
    private String name;
    private EmployeeType employeeType;
    private BigDecimal payRate; 
    private boolean isUnionMember;
    private boolean hasRetirement;

    
    private Employee(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.employeeType = builder.employeeType;
        this.payRate = builder.payRate;
        this.isUnionMember = builder.isUnionMember;
        this.hasRetirement = builder.hasRetirement;
    }
    
    
    public Employee(String id, String name, EmployeeType employeeType, BigDecimal payRate, boolean isUnionMember, boolean hasRetirement) {
        this.id = id;
        this.name = name;
        this.employeeType = employeeType;
        this.payRate = payRate;
        this.isUnionMember = isUnionMember;
        this.hasRetirement = hasRetirement;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public EmployeeType getEmployeeType() { return employeeType; }
    public BigDecimal getPayRate() { return payRate; }
    public boolean isUnionMember() { return isUnionMember; }
    public boolean hasRetirement() { return hasRetirement; }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + employeeType +
                ", payRate=" + payRate +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private EmployeeType employeeType;
        private BigDecimal payRate;
        private boolean isUnionMember;
        private boolean hasRetirement;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(EmployeeType type) {
            this.employeeType = type;
            return this;
        }
        
        public Builder fullTime() {
            this.employeeType = EmployeeType.FULL_TIME;
            return this;
        }
        
        public Builder partTime() {
            this.employeeType = EmployeeType.PART_TIME;
            return this;
        }
        
        public Builder contractor() {
            this.employeeType = EmployeeType.CONTRACTOR;
            return this;
        }

        public Builder payRate(BigDecimal payRate) {
            this.payRate = payRate;
            return this;
        }

        // Overload for ease of use
        public Builder payRate(double payRate) {
            this.payRate = BigDecimal.valueOf(payRate);
            return this;
        }

        public Builder isUnionMember(boolean isUnionMember) {
            this.isUnionMember = isUnionMember;
            return this;
        }
        
        public Builder withUnion() {
            this.isUnionMember = true;
            return this;
        }

        public Builder hasRetirement(boolean hasRetirement) {
            this.hasRetirement = hasRetirement;
            return this;
        }
        
        public Builder withRetirement() {
            this.hasRetirement = true;
            return this;
        }

        public Employee build() {
            if (id == null || id.isEmpty()) throw new IllegalStateException("Employee ID is required");
            if (name == null || name.isEmpty()) throw new IllegalStateException("Employee Name is required");
            if (employeeType == null) throw new IllegalStateException("Employee Type is required");
            if (payRate == null || payRate.compareTo(BigDecimal.ZERO) < 0) throw new IllegalStateException("Pay Rate cannot be negative");
            
            return new Employee(this);
        }
    }
}
