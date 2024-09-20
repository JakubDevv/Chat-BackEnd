package org.example.chatapp.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserAuthRequest(@NotBlank String username,
                              @NotBlank String password) {

}