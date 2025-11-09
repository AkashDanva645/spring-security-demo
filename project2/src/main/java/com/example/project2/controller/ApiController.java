package com.example.project2.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @GetMapping("/secured-endpoint")
    @PreAuthorize("hasAnyAuthority('SECURED_END_POINT')")
    public String securedEndpoint() {
        return "Hi " + SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
