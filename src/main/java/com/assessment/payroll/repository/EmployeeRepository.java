package com.assessment.payroll.repository;

import com.assessment.payroll.model.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    void save(Employee employee);

    Optional<Employee> findById(String id);

    List<Employee> findAll();
}
