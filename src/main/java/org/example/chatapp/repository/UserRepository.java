package org.example.chatapp.repository;

import jakarta.transaction.Transactional;
import org.example.chatapp.dto.user.UserNotificationDTO;
import org.example.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    Optional<User> findUserByUsername(String username);

    @Query("SELECT new org.example.chatapp.dto.user.UserNotificationDTO(n.id, u2.id, u2.first_name, u2.last_name, n.sent_time, n.group_name, g.id, n.notification_type) FROM Notification n LEFT JOIN User u ON u=n.receiver LEFT JOIN User u2 ON u2=n.sender LEFT JOIN Group g ON g.name = n.group_name WHERE u.username = ?1 ORDER BY n.id DESC")
    List<UserNotificationDTO> getNotifications(String username);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.id=?1")
    void deleteNotificationById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE GroupUser gu SET gu.unread_messages=0 WHERE gu.groupUserID.user_id.id=?2 AND gu.groupUserID.group_id.id=?1")
    void readAllMessagesByGroupIdAndUserId(Long group_id, Long user_id);
}
