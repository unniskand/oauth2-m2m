package com.example.oauth_demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
  
      @GetMapping("/public/hello")
      public Map<String, String> publicEndpoint() {
          Map<String, String> response = new HashMap<>();
          response.put("message", "Hello from a public endpoint! No authentication required.");
          return response;
      }
  
      @GetMapping("/private/hello")
      public Map<String, Object> privateEndpoint(@AuthenticationPrincipal Jwt jwt) {
          Map<String, Object> response = new HashMap<>();
          response.put("message", "Hello from a secured endpoint!");
          response.put("user", jwt.getSubject());
          response.put("claims", jwt.getClaims());
          return response;
      }
}
