package com.assessment.payroll.repository;

import com.assessment.payroll.model.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<String, Employee> dataStore = new ConcurrentHashMap<>();

    @Override
    public void save(Employee employee) {
        if (employee == null || employee.getId() == null) {
            throw new IllegalArgumentException("Employee or Employee ID cannot be null");
        }
        dataStore.put(employee.getId(), employee);
    }

    @Override
    public Optional<Employee> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(dataStore.get(id));
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(dataStore.values());
    }

    @Override
    public void deleteById(String id) {
        if (id != null) {
            dataStore.remove(id);
        }
    }
}
