package org.example.chatapp.dto;

import org.example.chatapp.model.enums.NotificationType;

import java.time.LocalDateTime;

public record GroupNotificationDTO(Long id,
                                   Long user_id,
                                   String user_firstName,
                                   String user_lastName,
                                   LocalDateTime sentTime,
                                   Long group_id,
                                   NotificationType notificationType) {
}
