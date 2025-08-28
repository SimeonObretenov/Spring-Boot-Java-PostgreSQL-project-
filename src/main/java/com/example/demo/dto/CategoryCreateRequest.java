package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank String name
) {}