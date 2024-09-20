package org.example.chatapp.model;

import jakarta.persistence.*;
import lombok.Data;
import org.example.chatapp.model.enums.FriendStatus;
import org.example.chatapp.model.id.FriendID;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "friends")
public class Friends {

    @EmbeddedId
    private FriendID friendID;

    @Enumerated(value = EnumType.STRING)
    private FriendStatus status;

    private LocalDateTime last_update;

}
