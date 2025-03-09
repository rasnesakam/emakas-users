package com.emakas.userService.controller;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("page")
public class UserMvcController {

    @GetMapping(value = {"", "/", "/{path:[^\\.]*}"})
    public String login(@PathVariable(value = "path", required = false) String path){
        return "forward:/index.html";
    }
}
