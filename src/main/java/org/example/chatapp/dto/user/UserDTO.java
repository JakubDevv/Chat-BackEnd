package org.example.chatapp.dto.user;

public record UserDTO(Long id, String firstName, String lastName, String email, String country, String city, Long unreadMessages) {
}
