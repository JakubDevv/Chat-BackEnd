package org.example.chatapp.dto.user;

public record UserCreatedDTO(Long id,
                            String refreshToken,
                            String accessToken) {

}
