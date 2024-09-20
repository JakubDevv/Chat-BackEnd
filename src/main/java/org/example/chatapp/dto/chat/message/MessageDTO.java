package org.example.chatapp.dto.chat.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.chatapp.model.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private Long id;
    private String content;
    private boolean photo;
    private String firstName;
    private String lastName;
    private Long userId;
    private LocalDateTime dateTime;
    private Long response_id;
    private String response_firstName;
    private String response_lastName;
    private Long response_user_id;
    private LocalDateTime response_dateTime;
    private String response_content;
    private Boolean response_deleted;
    private Boolean deleted;
    private List<Long> ids;
    private Long chat_id;
    private NotificationType notification_type;

    public MessageDTO(Long id, String content, boolean photo, String firstName, String lastName, Long userId, LocalDateTime dateTime, Long response_id, String response_firstName, String response_lastName, Long response_user_id, LocalDateTime response_dateTime, String response_content, NotificationType notification_type) {
        this.id = id;
        this.content = content;
        this.photo = photo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
        this.dateTime = dateTime;
        this.response_id = response_id;
        this.response_firstName = response_firstName;
        this.response_lastName = response_lastName;
        this.response_user_id = response_user_id;
        this.response_dateTime = response_dateTime;
        this.response_content = response_content;
        this.notification_type = notification_type;
    }

    public MessageDTO(Long id, String content, String firstName, String lastName, Long userId, LocalDateTime dateTime, NotificationType notification_type, Long chat_id) {
        this.id = id;
        this.content = content;
        this.photo = false;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
        this.dateTime = dateTime;
        this.ids = List.of();
        this.notification_type = notification_type;
        this.chat_id = chat_id;
    }

    public MessageDTO(Long id, String content, boolean photo, String firstName, String lastName, Long userId, LocalDateTime dateTime, Boolean deleted, List<Long> ids, Long chat_id, NotificationType notification_type) {
        this.id = id;
        this.content = content;
        this.photo = photo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
        this.dateTime = dateTime;
        this.deleted = deleted;
        this.ids = ids;
        this.chat_id = chat_id;
        this.notification_type = notification_type;
    }
}
