package org.example.springboot08security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 登录控制器
 *
 * @author booty
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(){

        return "login";
    }
}
