package com.example.SP26SE025.dtos;

// src/main/java/com/example/SP26SEO25/dtos/RegisterRequest.java
public record RegisterRequest(
    String email,
    String password,
    String fullName,
    String phone
) {}