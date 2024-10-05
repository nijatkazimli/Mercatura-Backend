package com.mercatura.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/")
    public String helloAdminController() {
        return "Hello Admin Controller";
    }
}
