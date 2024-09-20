package org.example.chatapp.dto.friends;

import org.example.chatapp.model.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

public record FriendShortDTO(Long id,
                             String bgColor,
                             String first_name,
                             String last_name,
                             String country,
                             String city,
                             Long mutualFriends,
                             Long mutualGroups,
                             UserStatus activityStatus) {
}
