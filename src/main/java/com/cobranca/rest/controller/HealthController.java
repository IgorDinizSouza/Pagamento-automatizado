package com.cobranca.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/status")
    public String status() {
        return "✅ Backend está no ar!";
    }
}