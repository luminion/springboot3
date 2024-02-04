package org.example.springboot08security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * hello控制器
 *
 * @author booty
 */
@RestController
public class TestController {

    @GetMapping("/anyone")
    public String hello(){
        return "success";
    }



    @PreAuthorize("hasRole('manager')")
    @GetMapping("/test_role")
    public String testRole(){
        return "success";
    }

    @PreAuthorize("hasAuthority('test_permission')")
    @GetMapping("/test_permission")
    public String world(){
        return "success";
    }



}
