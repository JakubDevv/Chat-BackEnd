package org.example.chatapp.dto.chat;

import org.example.chatapp.model.enums.NotificationType;
import org.example.chatapp.model.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ChatShortDTO(Long id,
                           LocalDateTime lastMessageTime,
                           String lastMessage,
                           Long unread_messages,
                           Long lastMessageUserId,
                           String lastMessageUser,
                           String chatName,
                           String bgColor,
                           Long userId,
                           boolean isChat,
                           Long activeUsers,
                           NotificationType notificationType) {
}
