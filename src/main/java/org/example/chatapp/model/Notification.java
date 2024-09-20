package org.example.chatapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.chatapp.model.enums.NotificationType;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    private LocalDateTime sent_time;

    private String group_name;

    @Enumerated(value = EnumType.STRING)
    private NotificationType notification_type;

    public Notification(User sender, User receiver, String group_name, NotificationType notification_type) {
        this.sender = sender;
        this.receiver = receiver;
        this.sent_time = LocalDateTime.now();
        this.group_name = group_name;
        this.notification_type = notification_type;
    }

    public Notification(User sender, User receiver, NotificationType notification_type) {
        this.sender = sender;
        this.receiver = receiver;
        this.sent_time = LocalDateTime.now();
        this.notification_type = notification_type;
    }

}
