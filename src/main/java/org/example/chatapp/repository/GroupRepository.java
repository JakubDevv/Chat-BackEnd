package org.example.chatapp.repository;

import jakarta.transaction.Transactional;
import org.example.chatapp.model.Group;
import org.example.chatapp.model.GroupUser;
import org.example.chatapp.model.User;
import org.example.chatapp.model.enums.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT u FROM User u JOIN GroupUser gu ON gu.groupUserID.user_id = u " +
            "WHERE gu.groupUserID.group_id.id = :groupId AND u.id != :userId AND gu.groupUserID.group_id.isduo = true")
    Optional<User> findUserByGroupId(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Query("SELECT g FROM User u JOIN GroupUser gu ON gu.groupUserID.user_id = u JOIN Group g ON gu.groupUserID.group_id = g " +
           "WHERE u.id = :userId")
    List<Group> findGroupsByUserId(Long userId);

    @Query("SELECT gu.unread_messages FROM GroupUser gu JOIN User u ON gu.groupUserID.user_id = u JOIN Group g ON gu.groupUserID.group_id = g WHERE u.id = ?1 AND g.id=?2")
    Long getUnreadMessagesByUserIdAndGroupId(Long user_id, Long group_id);

    @Query("SELECT gu FROM GroupUser gu JOIN User u ON gu.groupUserID.user_id = u JOIN Group g ON gu.groupUserID.group_id = g WHERE g.id = ?1 AND u.id<>?2")
    List<GroupUser> findUsersByGroupIdExceptUser(Long group_id, Long user_id);

    @Query("SELECT gu FROM GroupUser gu JOIN User u ON gu.groupUserID.user_id = u JOIN Group g ON gu.groupUserID.group_id = g WHERE g.id = ?1 AND u.id<>?2")
    GroupUser findUserByGroupIdExceptUser(Long group_id, Long user_id);

    @Query("SELECT gu FROM GroupUser gu JOIN User u ON gu.groupUserID.user_id = u JOIN Group g ON gu.groupUserID.group_id = g WHERE g.id = ?1")
    List<GroupUser> findUsersByGroupId(Long group_id);

    @Query(value = "SELECT COUNT(*) " +
            "FROM groups_users gu1 " +
            "JOIN groups_users gu2 ON gu1.group_id = gu2.group_id " +
            "JOIN groups g ON g.id = gu1.group_id " +
            "WHERE gu1.user_id = :userId1 AND gu2.user_id = :userId2 AND g.isduo = false", nativeQuery = true)
    Long countCommonGroups(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT COUNT(*) FROM User u JOIN GroupUser gu ON gu.groupUserID.user_id = u JOIN Group g ON gu.groupUserID.group_id = g WHERE g.id = ?1 AND u.last_activity > ?2")
    Long countOnlineUsersByGroupId(Long group_id, LocalDateTime activeThreshold);

    @Query("SELECT gu.role FROM GroupUser gu JOIN User u ON gu.groupUserID.user_id = u JOIN Group g ON gu.groupUserID.group_id = g WHERE g.id = ?1 AND u.id = ?2")
    GroupRole getUserRoleByGroupIdAndUserId(Long group_id, Long user_id);

    Optional<Group> getGroupByName(String name);

    @Query("SELECT g.id FROM Group g " +
            "JOIN GroupUser gu1 ON gu1.groupUserID.group_id.id = g.id " +
            "JOIN User u1 ON gu1.groupUserID.user_id.id = u1.id " +
            "JOIN GroupUser gu2 ON gu2.groupUserID.group_id.id = g.id " +
            "JOIN User u2 ON gu2.groupUserID.user_id.id = u2.id " +
            "WHERE u1.id = ?1 AND u2.id = ?2 AND g.isduo = true")
    Long findDuoGroupIdForUsers(Long userId1, Long userId2);


    @Query("SELECT count(*) FROM Message m " +
            "JOIN Group g ON g = m.group " +
            "WHERE g.id = ?1")
    Long countMessagesInChat(Long chatId);
}
