package org.example.chatapp.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ChatCreateDTO(@NotBlank String name, @NotEmpty List<Long> ids) {
}
