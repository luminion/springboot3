package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


/**
 * 1、自定义请求授权规则：http.authorizeHttpRequests
 * 2、自定义登录规则：http.formLogin
 * 3、自定义用户信息查询规则：UserDetailsService
 * 4、开启方法级别的精确权限控制：@EnableMethodSecurity + @PreAuthorize("hasAuthority('test_permission')")
 *
 * @author bootystar
 */
@EnableMethodSecurity
@Configuration
public class AppSecurityConfiguration {

    /**
     * 安全过滤链
     * 指定了请求授权规则、登录规则
     *
     * @param http http
     * @return {@code SecurityFilterChain }
     * @author booty
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //请求授权
        http.authorizeHttpRequests(registry -> {
            registry.requestMatchers("/").permitAll() //1、首页所有人都允许
                    .anyRequest().authenticated(); //2、剩下的任意请求都需要 认证（登录）
        });

        //表单登录
        //3、表单登录功能：开启默认表单登录功能；Spring Security提供默认登录页
        http.formLogin(formLogin -> {
            formLogin.loginPage("/login").permitAll(); //自定义登录页位置，并且所有人都能访问
        });

        return http.build();
    }

    /**
     * 用户详细信息服务
     * <p>
     * 此处采用将用户信息保存到内存中的方式，实际开发中需要从数据库中查询用户信息
     *
     * @param passwordEncoder 密码编码器 ,此处需要向容器中注入一个密码编码器, 否则会报错java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null
     * @return {@code UserDetailsService }
     * @author booty
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder  passwordEncoder){
        UserDetails u1 = User
                .withUsername("zhangsan")
                .password(passwordEncoder.encode("123456")) //使用密码加密器加密密码进行存储
                .roles("admin", "manager") // 角色
                .authorities("file_read", "test_permission") // 权限
                .build();


        UserDetails u2 = User.withUsername("lisi")
                .password(passwordEncoder.encode("123456"))
                .roles("manager")
                .authorities("file_read")
                .build();

        UserDetails u3= User.withUsername("wangwu")
                .password(passwordEncoder.encode("123456"))
                .build();



        //默认内存中保存所有用户信息的管理器, 实际应该使用JdbcUserDetailsManager,从数据库中查询用户信息
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager(u1, u2,u3);
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



}
