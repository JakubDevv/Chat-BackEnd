package org.example.chatapp.services;

import lombok.AllArgsConstructor;
import org.example.chatapp.aop.UpdateActivity;
import org.example.chatapp.exception.user.UserNotFoundException;
import org.example.chatapp.mapper.MessageMapper;
import org.example.chatapp.mapper.UserMapper;
import org.example.chatapp.model.*;
import org.example.chatapp.model.enums.GroupRole;
import org.example.chatapp.model.enums.NotificationType;
import org.example.chatapp.model.id.GroupUserID;
import org.example.chatapp.repository.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;

@AllArgsConstructor
@Service
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final GroupUserRepository groupUserRepository;
    private final GroupRepository groupRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserMapper userMapper;
    private final GroupService groupService;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @UpdateActivity
    @Override
    public void removeFriend(Principal principal, Long userId){
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        User friend = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        groupService.removeChat(groupRepository.findDuoGroupIdForUsers(user.getId(), friend.getId()), principal);
    }

    @UpdateActivity
    @Override
    public void acceptFriendRequest(Principal principal, Long userId){
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        User friend = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        friendRepository.acceptFriendRequest(user.getId(), userId);
        Group group = new Group(null, true);
        Group save = groupRepository.save(group);
        Notification notification = new Notification(user, friend, NotificationType.FRIEND_ACCEPT);
        Notification savedNotification = notificationRepository.save(notification);
        Message save2 = messageRepository.save(new Message(user, friend.getId().toString(), save, NotificationType.GROUP_CREATED));
        simpMessagingTemplate.convertAndSend("/topic/notifications/"+friend.getId(), userMapper.mapNotificationToUserNotificationDTO(savedNotification));
        simpMessagingTemplate.convertAndSend("/topic/user/"+friend.getId(), messageMapper.messageNotificationToMessageDTO(save2));
        groupUserRepository.save(new GroupUser(new GroupUserID(user, group), 1L, GroupRole.USER));
        groupUserRepository.save(new GroupUser(new GroupUserID(friend, group), 1L, GroupRole.USER));
    }

    @UpdateActivity
    @Override
    public void createFriendRequest(Principal principal, Long userId){
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        User friend = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Notification notification = new Notification(user, friend, NotificationType.FRIEND_REQUEST);
        Notification savedNotification = notificationRepository.save(notification);
        simpMessagingTemplate.convertAndSend("/topic/notifications/"+friend.getId(), userMapper.mapNotificationToUserNotificationDTO(savedNotification));
        friendRepository.createFriendRequest(user.getId(), userId);
    }
}
