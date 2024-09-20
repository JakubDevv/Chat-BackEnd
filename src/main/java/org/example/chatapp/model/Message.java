package org.example.chatapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.chatapp.model.enums.NotificationType;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "messages")
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender_id;

    private String content;

    private boolean image;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    private Boolean deleted;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "response_id")
    private Message response_id;

    @Enumerated(value = EnumType.STRING)
    private NotificationType notification_type;

    public Message(User sender_id, String content, boolean image, Group group, Message response_id) {
        this.sender_id = sender_id;
        this.content = content;
        this.image = image;
        this.sentAt = LocalDateTime.now();
        this.deleted = false;
        this.group = group;
        this.response_id = response_id;
    }

    public Message(User sender_id, String content, Group group, NotificationType notification_type) {
        this.sender_id = sender_id;
        this.content = content;
        this.image = false;
        this.sentAt = LocalDateTime.now();
        this.deleted = false;
        this.group = group;
        this.notification_type = notification_type;
    }
}
