package com.example.project1.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String sayHello() {
        return "Hello!";
    }

    @PostMapping("/")
    public String sayHelloPost() {
        return "Hello POST";
    }
}
