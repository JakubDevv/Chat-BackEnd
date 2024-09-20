package org.example.chatapp.services;

import lombok.RequiredArgsConstructor;
import org.example.chatapp.aop.UpdateActivity;
import org.example.chatapp.dto.chat.ChatCreateDTO;
import org.example.chatapp.dto.chat.ChatDTO;
import org.example.chatapp.dto.chat.ChatShortDTO;
import org.example.chatapp.dto.chat.ChatUserDTO;
import org.example.chatapp.dto.chat.message.MessageDTO;
import org.example.chatapp.dto.user.UserNotificationDTO;
import org.example.chatapp.exception.chat.ChatNotFoundException;
import org.example.chatapp.exception.user.UserNotFoundException;
import org.example.chatapp.mapper.ChatMapper;
import org.example.chatapp.mapper.MessageMapper;
import org.example.chatapp.mapper.UserMapper;
import org.example.chatapp.model.*;
import org.example.chatapp.model.enums.FriendStatus;
import org.example.chatapp.model.enums.GroupRole;
import org.example.chatapp.model.enums.NotificationType;
import org.example.chatapp.model.enums.UserStatus;
import org.example.chatapp.model.id.GroupUserID;
import org.example.chatapp.repository.*;
import org.example.chatapp.s3.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GroupServiceImpl implements GroupService {

    @Value("${bucket.name}")
    private String bucketName;

    private final GroupRepository groupRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final GroupUserRepository groupUserRepository;
    private final FriendRepository friendRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;
    private final UserMapper userMapper;
    private final S3Service s3Service;
    private final MessageMapper messageMapper;
    private final ChatMapper chatMapper;

    public GroupServiceImpl(GroupRepository groupRepository, MessageRepository messageRepository, UserRepository userRepository, GroupUserRepository groupUserRepository, FriendRepository friendRepository, SimpMessagingTemplate simpMessagingTemplate, NotificationRepository notificationRepository, UserMapper userMapper, S3Service s3Service, MessageMapper messageMapper, ChatMapper chatMapper) {
        this.groupRepository = groupRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.groupUserRepository = groupUserRepository;
        this.friendRepository = friendRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.notificationRepository = notificationRepository;
        this.userMapper = userMapper;
        this.s3Service = s3Service;
        this.messageMapper = messageMapper;
        this.chatMapper = chatMapper;
    }

    @UpdateActivity
    @Override
    public List<ChatShortDTO> getChats(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return groupRepository.findGroupsByUserId(user.getId()).stream().map(group -> chatMapper.chatToChatShortDTO(group, user)).sorted((a, b) -> {
            if (a.lastMessageTime() == null && b.lastMessageTime() == null) {
                return 0;
            } else if (a.lastMessageTime() == null) {
                return 1;
            } else if (b.lastMessageTime() == null) {
                return -1;
            } else {
                return b.lastMessageTime().compareTo(a.lastMessageTime());
            }
        }).toList();
    }

    @UpdateActivity
    @Override
    public List<MessageDTO> getMessagesByChatId(Long id, Principal principal) {
        User userrr = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        userRepository.readAllMessagesByGroupIdAndUserId(id, userrr.getId());
        simpMessagingTemplate.convertAndSend("/topic/messages/2/"+id, userrr.getId());
        List<MessageDTO> messages = messageRepository.findAllByGroup_IdOrderBySentAt(id).stream().map(messageMapper::messageToMessageDTO).toList();
        groupUserRepository.getUnreadMessagesByGroup(id).forEach(user -> {
            if(!messages.isEmpty() && user.getUnread_messages() != messages.size()){
                MessageDTO message = messages.get(messages.size() - Math.toIntExact(user.getUnread_messages()) - 1);
                List<Long> ids = new ArrayList<>(message.getIds());
                ids.add(user.getGroupUserID().getUser_id().getId());
                message.setIds(ids);
            }
        });
        return messages;
    }

    @UpdateActivity
    @Override
    public ChatDTO getChatById(Long id, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        Group group = groupRepository.findById(id).orElseThrow(() -> new ChatNotFoundException(id));
        String chatName = "";
        if(group.getName() == null){
            User user1 = groupRepository.findUserByGroupId(id, user.getId()).orElseThrow(UserNotFoundException::new);
            chatName = user1.getFirst_name() + " " + user1.getLast_name();
        }else {
            chatName = group.getName();
        }
        List<ChatUserDTO> users = groupUserRepository.findUsersByGroup_Id(id).stream().map(userr -> {
            Friends friends = friendRepository.getFriendStatus(userr.getId(), user.getId()).orElse(null);
            FriendStatus status = (friends == null || friends.getStatus() == null) ? null : friends.getStatus();;
            if(friends != null){
                if(Objects.equals(friends.getFriendID().getUser_id().getId(), userr.getId()) && friends.getStatus().equals(FriendStatus.PENDING)){
                    status = FriendStatus.TO_ACCEPT;
                }
            }
            return new ChatUserDTO(userr.getId(), userr.getFirst_name(), userr.getLast_name(), status, messageRepository.countAllByGroup_IdAndSender_idEquals(id, userr), friendRepository.countCommonFriends(userr.getId(), user.getId()), userr.getLast_activity().isAfter(LocalDateTime.now().minusMinutes(2)) ? UserStatus.ONLINE : UserStatus.OFFLINE, userr.getBgcolor());
        }).toList();
        return new ChatDTO(group.getId(), chatName, group.isIsduo(),new ArrayList<>(), messageRepository.countAllByGroup_Id(id), group.getCreated(), groupRepository.getUserRoleByGroupIdAndUserId(id, user.getId()), users, getMessagesByChatId(group.getId(), principal));
    }

    @UpdateActivity
    @Override
    public Long createChat(ChatCreateDTO chatCreateDTO, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        Group group = new Group(chatCreateDTO.name(), false);
        Group save = groupRepository.save(group);
        groupUserRepository.save(new GroupUser(new GroupUserID(user, save), GroupRole.OWNER));
        List<String> users = new ArrayList<>();
        for(Long id : chatCreateDTO.ids()){
            User user2 = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
            users.add(user2.getFirst_name() + " " + user2.getLast_name());
            groupUserRepository.save(new GroupUser(new GroupUserID(user2, save), GroupRole.USER, 1L));
            Notification notification = new Notification(user, user2, save.getName(), NotificationType.GROUP_ADDED);
            Notification savedNotification = notificationRepository.save(notification);
            simpMessagingTemplate.convertAndSend("/topic/notifications/"+user2.getId(), userMapper.mapNotificationToUserNotificationDTO(savedNotification));
        }
        messageRepository.save(new Message(user, String.join(", ", users), group, NotificationType.GROUP_CREATED));
        return save.getId();
    }

    @Override
    public void removeChat(Long chatId, Principal principal) {
        User userr = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        Group group = groupRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException(chatId));
        if (group.isIsduo()) {
            GroupUser friend = groupRepository.findUserByGroupIdExceptUser(group.getId(), userr.getId());
            friendRepository.removeFriend(userr.getId(), friend.getGroupUserID().getUser_id().getId());
            groupUserRepository.delete(friend);
            groupUserRepository.removeUserFromChat(userr.getId(), group.getId());
            Notification notification = new Notification(userr, friend.getGroupUserID().getUser_id(), NotificationType.FRIEND_REMOVED);
            Notification savedNotification = notificationRepository.save(notification);
            simpMessagingTemplate.convertAndSend("/topic/notifications/"+friend.getGroupUserID().getUser_id().getId(), userMapper.mapNotificationToUserNotificationDTO(savedNotification));
        } else {
            List<GroupUser> groupUsers = groupUserRepository.findAllGroupUserByGroupId(chatId);
            groupUsers.forEach(user -> {
                if(!Objects.equals(user.getGroupUserID().getUser_id().getId(), userr.getId())){
                    Notification notification = new Notification(userr, user.getGroupUserID().getUser_id(), group.getName(), NotificationType.GROUP_REMOVED);
                    Notification savedNotification = notificationRepository.save(notification);
                    simpMessagingTemplate.convertAndSend("/topic/notifications/"+user.getGroupUserID().getUser_id().getId(), userMapper.mapNotificationToUserNotificationDTO(savedNotification));
                }
                groupUserRepository.delete(user);
            });
        }
        messageRepository.removeAllByGroup(group);
        groupRepository.delete(group);
    }

    @Override
    public ResponseEntity<?> getChatPictureById(Long id) {
        try {
            byte[] image = s3Service.getObject(
                    bucketName,
                    "ChatImages/" + id
            );
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(image);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void sendFile(Principal principal, Long chatId, MultipartFile file) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        Group group = groupRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException(chatId));

        Message message = new Message(user, "Sent Message", true, group, null);
        Message save = messageRepository.save(message);
        try {
            s3Service.putObject(
                    bucketName,
                    "Messages/" + save.getId(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
        MessageDTO messageDTO = messageMapper.messageFileToMessageDTO(save, user);
        groupRepository.findUsersByGroupId(chatId).forEach(userr -> {
            userr.setUnread_messages(userr.getUnread_messages() + 1L);
            groupUserRepository.save(userr);
            simpMessagingTemplate.convertAndSend("/topic/user/"+userr.getGroupUserID().getUser_id().getId(), messageDTO);
        });
    }

    @Override
    public ResponseEntity<?> getFile(Principal principal, Long messageId) {
        try {
            byte[] image = s3Service.getObject(
                    bucketName,
                    "Messages/" + messageId
            );
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(image);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void removeUserFromChat(Long chatId, Long userId, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        User user2 = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Group group = groupRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException(chatId));
        Notification notification = new Notification(user, user2, group.getName(), NotificationType.GROUP_KICKED);
        Notification savedNotification = notificationRepository.save(notification);
        simpMessagingTemplate.convertAndSend("/topic/notifications/"+user2.getId(), userMapper.mapNotificationToUserNotificationDTO(savedNotification));
        groupUserRepository.removeUserFromChat(userId, chatId);
        Message save = messageRepository.save(new Message(user2, user2.getFirst_name() + " " + user2.getLast_name(), group, NotificationType.GROUP_KICKED));
        groupRepository.findUsersByGroupId(chatId).forEach(users -> {
            users.setUnread_messages(users.getUnread_messages() + 1L);
            groupUserRepository.save(users);
            simpMessagingTemplate.convertAndSend("/topic/user/"+users.getGroupUserID().getUser_id().getId(), messageMapper.messageNotificationToMessageDTO(save));
        });
    }

    @Override
    public void addUserToChat(Principal principal, Long chatId, Long userId) {
        User userr = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Group group = groupRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException(chatId));
        GroupUser groupUser = new GroupUser(new GroupUserID(user, group), groupRepository.countMessagesInChat(chatId));
        groupUserRepository.save(groupUser);
        Notification notification = new Notification(userr, user, group.getName(), NotificationType.GROUP_ADDED);
        Notification savedNotification = notificationRepository.save(notification);
        Message save = messageRepository.save(new Message(user, user.getFirst_name() + " " + user.getLast_name(), group, NotificationType.GROUP_ADDED));
        simpMessagingTemplate.convertAndSend("/topic/notifications/"+user.getId(), userMapper.mapNotificationToUserNotificationDTO(savedNotification));
        groupRepository.findUsersByGroupId(chatId).forEach(users -> {
            users.setUnread_messages(users.getUnread_messages() + 1L);
            groupUserRepository.save(users);
            simpMessagingTemplate.convertAndSend("/topic/user/"+users.getGroupUserID().getUser_id().getId(), messageMapper.messageNotificationToMessageDTO(save));
        });
    }

    @Override
    public void setChatPicture(Principal principal, Long chatId, MultipartFile file) {
        try {
            s3Service.putObject(
                    bucketName,
                    "ChatImages/" + chatId,
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void leaveChat(Long chatId, Principal principal) {
        User userr = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        groupUserRepository.removeUserFromChat(userr.getId(), chatId);
        Group group = groupRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException(chatId));
        Message save = messageRepository.save(new Message(userr, userr.getFirst_name() + " " + userr.getLast_name(), group, NotificationType.GROUP_LEFT));
        groupRepository.findUsersByGroupId(chatId).forEach(users -> {
            users.setUnread_messages(users.getUnread_messages() + 1L);
            groupUserRepository.save(users);
            simpMessagingTemplate.convertAndSend("/topic/user/"+users.getGroupUserID().getUser_id().getId(), messageMapper.messageNotificationToMessageDTO(save));
        });
    }

    @Override
    public List<UserNotificationDTO> getNotifications(Long chatId, Principal principal) {
        Group group = groupRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException(chatId));
        User groupOwner = groupUserRepository.findGroupOwner(chatId);
        UserNotificationDTO notification = new UserNotificationDTO(0L, groupOwner.getId(), groupOwner.getFirst_name(), groupOwner.getLast_name(), group.getCreated(), group.getName(), group.getId(),NotificationType.GROUP_CREATED);
        List<UserNotificationDTO> noti = notificationRepository.getNotificationsByChat(group.getName());
        noti.add(notification);
        return noti;
    }

}
