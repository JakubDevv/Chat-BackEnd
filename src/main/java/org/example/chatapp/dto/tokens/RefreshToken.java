package org.example.chatapp.dto.tokens;

import jakarta.validation.constraints.NotBlank;

public record RefreshToken(@NotBlank String value) {

}