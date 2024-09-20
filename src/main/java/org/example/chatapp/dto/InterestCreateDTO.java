package org.example.chatapp.dto;

import jakarta.validation.constraints.NotBlank;

public record InterestCreateDTO(@NotBlank String value) {
}
