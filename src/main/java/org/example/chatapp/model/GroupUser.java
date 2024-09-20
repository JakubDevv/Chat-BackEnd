package org.example.chatapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.chatapp.model.enums.GroupRole;
import org.example.chatapp.model.id.GroupUserID;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "groups_users")
@NoArgsConstructor
public class GroupUser {

    @EmbeddedId
    private GroupUserID groupUserID;

    private Long unread_messages;

    @Enumerated(value = EnumType.STRING)
    private GroupRole role;

    private LocalDateTime added;

    public GroupUser(GroupUserID groupUserID, Long unread_messages, GroupRole role) {
        this.groupUserID = groupUserID;
        this.unread_messages = unread_messages;
        this.role = role;
        this.added = LocalDateTime.now();
    }

    public GroupUser(GroupUserID groupUserID, GroupRole role) {
        this.groupUserID = groupUserID;
        this.unread_messages = 0L;
        this.role = role;
        this.added = LocalDateTime.now();
    }

    public GroupUser(GroupUserID groupUserID, GroupRole role, Long unread_messages) {
        this.groupUserID = groupUserID;
        this.unread_messages = unread_messages;
        this.role = role;
        this.added = LocalDateTime.now();
    }

    public GroupUser(GroupUserID groupUserID) {
        this.groupUserID = groupUserID;
        this.unread_messages = 0L;
        this.role = GroupRole.USER;
        this.added = LocalDateTime.now();
    }

    public GroupUser(GroupUserID groupUserID, Long unread_messages) {
        this.groupUserID = groupUserID;
        this.unread_messages = unread_messages;
        this.role = GroupRole.USER;
        this.added = LocalDateTime.now();
    }
}
