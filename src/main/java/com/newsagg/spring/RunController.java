package com.newsagg.spring;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RunController {

    @GetMapping("/hello")
    public String hello(ModelMap model) {
        GetData gd = new GetData();
        model.addAttribute("news", gd.getData());
        return "index";
    }

}