package com.example.doc.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.doc.entity.Employee;
import com.example.doc.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author booty
 */
@Tag(name = "员工",description = "员工CRUD")
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/emp/{id}")
    public Employee getEmployee(@PathVariable("id") Long id){
        return employeeService.getEmployeeById(id);
    }

    @GetMapping("/empList")
    public List<Employee> getEmployee(){
        return employeeService.getEmployees();
    }

    @PostMapping("/emp")
    public String saveEmployee(@RequestBody Employee employee){
        employeeService.saveEmployee(employee);
        return "ok";
    }

    @DeleteMapping("/emp/{id}")
    public String deleteEmployee(@PathVariable("id") Long id){
        employeeService.deleteEmployee(id);
        return "ok";
    }
}
