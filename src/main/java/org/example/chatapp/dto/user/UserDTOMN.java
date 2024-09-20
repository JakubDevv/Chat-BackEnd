package org.example.chatapp.dto.user;

import org.example.chatapp.model.enums.FriendStatus;

import java.time.LocalDateTime;

public record UserDTOMN(Long id,
                        FriendStatus status,
                        String firstName,
                        String lastName,
                        LocalDateTime joined,
                        Long mutualFriends) {
}
