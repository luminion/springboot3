package org.example.springboot10reactor.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理程序, 和 @ControllerAdvice 一样，但是 @RestControllerAdvice 是针对 @RestController 的，
 * 和MVC基本无区别
 *
 * @author booty
 */
@RestControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(ArithmeticException.class)
    public String error(ArithmeticException exception){
        System.out.println("发生了数学运算异常"+exception);

        //返回这些进行错误处理；
//        ProblemDetail：
//        ErrorResponse ：

        return "炸了，哈哈...";
    }
}
