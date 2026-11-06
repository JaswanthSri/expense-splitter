package com.example.expensesplitter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SystemController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "application", "expense-splitter"
        );
    }

    @GetMapping("/version")
    public Map<String, String> version() {
        return Map.of(
                "application", "expense-splitter",
                "version", "1.0.0"
        );
    }
}