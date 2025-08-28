package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record TagCreateRequest(
        @NotBlank String name
) {}