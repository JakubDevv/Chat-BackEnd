package org.example.chatapp.mapper;

import lombok.AllArgsConstructor;
import org.example.chatapp.dto.chat.ChatShortDTO;
import org.example.chatapp.exception.user.UserNotFoundException;
import org.example.chatapp.model.Group;
import org.example.chatapp.model.Message;
import org.example.chatapp.model.User;
import org.example.chatapp.repository.GroupRepository;
import org.example.chatapp.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class ChatMapper {

    private final GroupRepository groupRepository;
    private final MessageRepository messageRepository;

    public ChatShortDTO chatToChatShortDTO(Group group, User user){
        Message message = messageRepository.findFirstByGroupIdOrderBySentAtDesc(group.getId()).orElse(null);
        User friend = new User();
        if(group.isIsduo()){
            friend = groupRepository.findUserByGroupId(group.getId(), user.getId()).orElseThrow(UserNotFoundException::new);
        }

        return new ChatShortDTO(
                group.getId(),
                message != null ? message.getSentAt() : null,
                message != null ? message.getContent() : null,
                groupRepository.getUnreadMessagesByUserIdAndGroupId(user.getId(), group.getId()),
                message != null ? message.getSender_id().getId() : null,
                message != null ? message.getSender_id().getFirst_name() + " " + message.getSender_id().getLast_name()  : null,
                group.isIsduo() ? friend.getFirst_name() + " " + friend.getLast_name() : group.getName(),
                group.isIsduo() ? friend.getBgcolor() : "",
                friend.getId(),
                group.isIsduo(),
                groupRepository.countOnlineUsersByGroupId(group.getId(), LocalDateTime.now().minusMinutes(2L)) - 1L,
                message != null ? message.getNotification_type() : null);
    }
}
