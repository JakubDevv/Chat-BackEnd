package org.example.chatapp.repository;

import jakarta.transaction.Transactional;
import org.example.chatapp.model.Group;
import org.example.chatapp.model.GroupUser;
import org.example.chatapp.model.User;
import org.example.chatapp.model.id.GroupUserID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, GroupUserID> {

    @Query("SELECT SUM(gu.unread_messages) FROM GroupUser gu where gu.groupUserID.user_id = ?1")
    Long getUnreadMessagesByUser(User user);

    @Query("SELECT gu FROM GroupUser gu where gu.groupUserID.group_id.id = ?1")
    List<GroupUser> getUnreadMessagesByGroup(Long groupId);

    @Query("SELECT gu FROM GroupUser gu where gu.groupUserID.group_id.id = ?1 and gu.groupUserID.user_id.id = ?2")
    GroupUser getGroupUserByGroupIdAndUserId(Long groupId, Long userId);

    @Query("SELECT u FROM GroupUser gu JOIN gu.groupUserID.user_id u JOIN gu.groupUserID.group_id g WHERE g.id = ?1")
    List<User> findUsersByGroup_Id(Long groupId);

    @Query("SELECT COUNT(u) FROM GroupUser gu JOIN gu.groupUserID.user_id u JOIN gu.groupUserID.group_id g WHERE u.id = ?1 AND g.isduo=false")
    Long countAllGroupsByUser_Id(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM GroupUser gu WHERE gu.groupUserID.user_id.id=?1 AND gu.groupUserID.group_id.id=?2")
    void removeUserFromChat(Long user_id, Long chat_id);

    @Query("SELECT gu FROM GroupUser gu WHERE gu.groupUserID.group_id.id=:chat_id")
    List<GroupUser> findAllGroupUserByGroupId(Long chat_id);

    @Query("SELECT u FROM User u LEFT JOIN GroupUser gu ON gu.groupUserID.user_id = u LEFT JOIN Group g ON g.id=gu.groupUserID.group_id.id WHERE g.id = ?1 AND gu.role='OWNER'")
    User findGroupOwner(Long chat_id);
}
