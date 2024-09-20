package org.example.chatapp.services;

import org.example.chatapp.dto.chat.ChatCreateDTO;
import org.example.chatapp.dto.chat.ChatDTO;
import org.example.chatapp.dto.chat.ChatShortDTO;
import org.example.chatapp.dto.chat.message.MessageDTO;
import org.example.chatapp.dto.user.UserNotificationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface GroupService {

    List<ChatShortDTO> getChats(Principal principal);

    List<MessageDTO> getMessagesByChatId(Long id, Principal principal);

    ChatDTO getChatById(Long id, Principal principal);

    Long createChat(ChatCreateDTO chatCreateDTO, Principal principal);

    void removeChat(Long chatId, Principal principal);

    ResponseEntity<?> getChatPictureById(Long id);

    void sendFile(Principal principal, Long chatId, MultipartFile file);

    ResponseEntity<?> getFile(Principal principal, Long messageId);

    void removeUserFromChat(Long chatId, Long userId, Principal principal);

    void addUserToChat(Principal principal, Long chatId, Long userId);

    void setChatPicture(Principal principal, Long chatId, MultipartFile file);

    void leaveChat(Long chatId, Principal principal);

    List<UserNotificationDTO> getNotifications(Long chatId, Principal principal);
}
