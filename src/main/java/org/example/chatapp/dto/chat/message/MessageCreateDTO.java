package org.example.chatapp.dto.chat.message;

public record MessageCreateDTO(Long sender_id, String content, Long response_id, boolean media) {
}
