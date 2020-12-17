package com.wolox.training.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookController {

    @GetMapping(path = "greeting")
    public String greeting(@RequestParam(name = "name", defaultValue = "world", required = false) String name,
                           Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }
}

