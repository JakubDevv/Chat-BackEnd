package org.example.chatapp.repository;

import org.example.chatapp.dto.user.UserNotificationDTO;
import org.example.chatapp.model.Message;
import org.example.chatapp.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {


    @Query("SELECT new org.example.chatapp.dto.user.UserNotificationDTO(n.id, u.id, u.first_name, u.last_name, n.sent_time, n.group_name, g.id, n.notification_type) FROM Notification n LEFT JOIN User u ON u=n.receiver LEFT JOIN User u2 ON u2=n.sender LEFT JOIN Group g ON g.name = n.group_name WHERE n.group_name = ?1 ORDER BY n.id DESC")
    List<UserNotificationDTO> getNotificationsByChat(String chatName);

}
