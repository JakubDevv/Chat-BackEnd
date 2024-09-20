package org.example.chatapp.dto.user;

import org.example.chatapp.model.enums.NotificationType;

import java.time.LocalDateTime;

public record UserNotificationDTO(Long id,
                                  Long user_id,
                                  String user_firstName,
                                  String user_lastName,
                                  LocalDateTime sentTime,
                                  String group_name,
                                  Long group_id,
                                  NotificationType notificationType) {
}
