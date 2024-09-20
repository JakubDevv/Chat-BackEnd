package org.example.chatapp.websocket;

import jakarta.transaction.Transactional;
import org.example.chatapp.dto.IdDTO;
import org.example.chatapp.dto.chat.message.MessageCreateDTO;
import org.example.chatapp.dto.chat.message.MessageDTO;
import org.example.chatapp.model.Group;
import org.example.chatapp.model.GroupUser;
import org.example.chatapp.model.Message;
import org.example.chatapp.model.User;
import org.example.chatapp.repository.GroupRepository;
import org.example.chatapp.repository.GroupUserRepository;
import org.example.chatapp.repository.MessageRepository;
import org.example.chatapp.repository.UserRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@Controller
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;

    public ChatController(SimpMessagingTemplate simpMessagingTemplate, UserRepository userRepository, MessageRepository messageRepository, GroupRepository groupRepository, GroupUserRepository groupUserRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.groupRepository = groupRepository;
        this.groupUserRepository = groupUserRepository;
    }

    @MessageMapping("/chat/{to}")
    @Transactional
    public void sendMessage(@DestinationVariable Long to, MessageCreateDTO messageCreateDTO){
        User user = userRepository.findById(messageCreateDTO.sender_id()).orElseThrow(IllegalArgumentException::new);
        Group group = groupRepository.findById(to).orElseThrow(IllegalArgumentException::new);
        Message responeMessage = messageRepository.findById(messageCreateDTO.response_id()).orElse(null);
        Message message = new Message(user, messageCreateDTO.content(), messageCreateDTO.media(), group, responeMessage);
        Message save = messageRepository.save(message);
        MessageDTO messageDTO = new MessageDTO(save.getId(), save.getContent(), save.isImage(), user.getFirst_name(), user.getLast_name(), user.getId(), save.getSentAt(), responeMessage==null ? null : save.getResponse_id().getId(), responeMessage==null ? null : save.getResponse_id().getSender_id().getFirst_name(), responeMessage==null ? null : save.getResponse_id().getSender_id().getLast_name(), responeMessage==null ? null : save.getResponse_id().getSender_id().getId(), responeMessage==null ? null : save.getResponse_id().getSentAt(), responeMessage==null ? null : save.getResponse_id().getContent(), responeMessage==null ? null : save.getResponse_id().getDeleted(), save.getDeleted(), List.of(), to, null);
        groupRepository.findUsersByGroupId(to).forEach(userr -> {
            userr.setUnread_messages(userr.getUnread_messages() + 1L);
            groupUserRepository.save(userr);
            simpMessagingTemplate.convertAndSend("/topic/user/"+userr.getGroupUserID().getUser_id().getId(), messageDTO);
        });
    }

    @MessageMapping("/chat/messages/{to}")
    public void readMessage(@DestinationVariable Long to, IdDTO user_id){
        GroupUser groupUser = groupUserRepository.getGroupUserByGroupIdAndUserId(to, user_id.getId());
        groupUser.setUnread_messages(0L);
        groupUserRepository.save(groupUser);
        simpMessagingTemplate.convertAndSend("/topic/messages/2/"+to, user_id.getId());
    }

    @MessageMapping("/chat/messages/delete/{to}")
    public void deleteMessage(@DestinationVariable Long to, IdDTO user_id){
        Message message = messageRepository.findById(user_id.getId()).orElseThrow();
        message.setDeleted(true);
        messageRepository.save(message);
        simpMessagingTemplate.convertAndSend("/topic/messages/3/"+to, user_id.getId());
    }
}
