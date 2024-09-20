package org.example.chatapp.dto.chat;

import org.example.chatapp.dto.GroupNotificationDTO;
import org.example.chatapp.dto.chat.message.MessageDTO;
import org.example.chatapp.dto.user.UserNotificationDTO;
import org.example.chatapp.model.enums.GroupRole;

import java.time.LocalDateTime;
import java.util.List;

public record ChatDTO(Long id,
                      String name,
                      boolean duo,
                      List<String> images,
                      Long messages_amount,
                      LocalDateTime created,
                      GroupRole role,
                      List<ChatUserDTO> users,
                      List<MessageDTO> messages) {

}
