package com.emakas.userService.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class UserMvcController {

    @RequestMapping("/login")
    public String login(Model model){

        return "login";
    }
}
