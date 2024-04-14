package org.example.springboot10reactor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;

/**
 * Rendering对象相当于MVC中的ModelAndView
 * 用于前后端一体开发
 *
 * @author booty
 */
@Controller
public class RenderingController {

    /**
     * 跳转到指定首页(无首页模板对象,会报404)
     *
     * @return {@link Rendering }
     * @author booty
     */
    @GetMapping("/go2index")
    public Rendering go2index(){
        return Rendering.redirectTo("index").build();
    }

    /**
     * 跳转到百度
     *
     * @return {@link Rendering }
     * @author booty
     */
    @GetMapping("/go2baidu")
    public Rendering go2baidu(){
        return Rendering.redirectTo("https://www.baidu.com").build();
    }
}
