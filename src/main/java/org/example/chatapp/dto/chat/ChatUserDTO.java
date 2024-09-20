package org.example.chatapp.dto.chat;

import org.example.chatapp.model.enums.FriendStatus;
import org.example.chatapp.model.enums.UserStatus;

public record ChatUserDTO(Long id, String firstName, String lastName, FriendStatus friendStatus, Long messages, Long mutualFriends, UserStatus userStatus, String bgColor) {
}
