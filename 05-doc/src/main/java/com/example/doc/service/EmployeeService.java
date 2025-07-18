package com.example.doc.service;

import com.example.doc.entity.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author bootystar
 */
@Service
public class EmployeeService {

    Map<Long, Employee> data = new ConcurrentHashMap<>();

    public Employee getEmployeeById(Long id) {
        return data.get(id);
    }

    public List<Employee> getEmployees() {
        return data.values().stream().toList();
    }

    public void saveEmployee(Employee employee) {
        data.put(employee.getId(),employee);
    }

    public void deleteEmployee(Long id) {
        data.remove(id);
    }
}
