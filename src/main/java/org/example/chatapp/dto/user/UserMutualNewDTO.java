package org.example.chatapp.dto.user;

import org.example.chatapp.model.enums.UserStatus;

public record UserMutualNewDTO(UserDTOMN mutual, UserDTOMN newUsers) {
}
