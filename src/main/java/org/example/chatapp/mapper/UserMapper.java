package org.example.chatapp.mapper;

import lombok.AllArgsConstructor;
import org.example.chatapp.dto.InterestDTO;
import org.example.chatapp.dto.friends.FriendDTO;
import org.example.chatapp.dto.friends.FriendShortDTO;
import org.example.chatapp.dto.user.UserNotificationDTO;
import org.example.chatapp.model.Group;
import org.example.chatapp.model.Notification;
import org.example.chatapp.model.User;
import org.example.chatapp.model.enums.FriendStatus;
import org.example.chatapp.model.enums.UserStatus;
import org.example.chatapp.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserMapper {

    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final FriendRepository friendRepository;
    private final GroupUserRepository groupUserRepository;
    private final GroupRepository groupRepository;

    public FriendDTO mapUserToFriendDTO(Long userId, FriendStatus status, Long userId2){
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        return new FriendDTO(
                userId,
                user.getBgcolor(),
                user.getFirst_name(),
                user.getLast_name(),
                user.getCountry(),
                user.getCity(),
                status,
                Period.between(user.getBirth().toLocalDate(), LocalDate.now()).getYears(),
                interestRepository.findAllByUser_Username(user.getUsername()).stream().map(InterestDTO::value).toList(),
                friendRepository.getAllFriends(userId),
                friendRepository.countCommonFriends(userId, userId2),
                groupUserRepository.countAllGroupsByUser_Id(userId),
                groupRepository.countCommonGroups(userId, userId2),
                user.getCreated(),
                user.getLast_activity().isAfter(LocalDateTime.now().minusMinutes(2)) ? UserStatus.ONLINE : UserStatus.OFFLINE);
    }

    public FriendShortDTO mapUserToFriendShortDTO(Long userId, Long userId2){
        User user = userRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        return new FriendShortDTO(
                userId,
                user.getBgcolor(),
                user.getFirst_name(),
                user.getLast_name(),
                user.getCountry(),
                user.getCity(),
                friendRepository.countCommonFriends(userId, userId2),
                groupRepository.countCommonGroups(userId, userId2),
                user.getLast_activity().isAfter(LocalDateTime.now().minusMinutes(2)) ? UserStatus.ONLINE : UserStatus.OFFLINE);
    }

    public UserNotificationDTO mapNotificationToUserNotificationDTO(Notification notification){
        return new UserNotificationDTO(
                notification.getId(),
                notification.getSender().getId(),
                notification.getSender().getFirst_name(),
                notification.getSender().getLast_name(),
                notification.getSent_time(),
                notification.getGroup_name(),
                notification.getNotification_type().toString().startsWith("GROUP") ? groupRepository.getGroupByName(notification.getGroup_name()).map(Group::getId).orElse(null) : null,
                notification.getNotification_type());
    }
}
