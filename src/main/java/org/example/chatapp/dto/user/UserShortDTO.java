package org.example.chatapp.dto.user;

import org.example.chatapp.model.enums.FriendStatus;

import java.time.LocalDateTime;

public record UserShortDTO(Long id, String firstName, String lastName, FriendStatus friendStatus, LocalDateTime created, Long mutualFriends) {
}
