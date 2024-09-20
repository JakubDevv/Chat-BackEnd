package org.example.chatapp.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.example.chatapp.dto.chat.ChatCreateDTO;
import org.example.chatapp.dto.chat.ChatDTO;
import org.example.chatapp.dto.chat.ChatShortDTO;
import org.example.chatapp.dto.chat.message.MessageDTO;
import org.example.chatapp.dto.user.UserNotificationDTO;
import org.example.chatapp.services.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/chats")
    private ResponseEntity<List<ChatShortDTO>> getChats(Principal principal){
        return new ResponseEntity<>(groupService.getChats(principal), HttpStatus.OK);
    }

    @GetMapping("/chats2/{id}")
    private ResponseEntity<ChatDTO> getChatById(@Min(1) @PathVariable Long id, Principal principal){
        return new ResponseEntity<>(groupService.getChatById(id, principal), HttpStatus.OK);
    }

    @GetMapping("/picture/{id}")
    private ResponseEntity<?> getChatPictureById(@Min(1) @PathVariable Long id){
        return groupService.getChatPictureById(id);
    }

    @PostMapping
    private ResponseEntity<Long> createChat(@Valid @RequestBody ChatCreateDTO chatCreateDTO, Principal principal){
        return new ResponseEntity<>(groupService.createChat(chatCreateDTO, principal), HttpStatus.OK);
    }

    @DeleteMapping
    private ResponseEntity<Void> removeChat(@Min(1) @RequestParam Long chat_id, Principal principal){
        groupService.removeChat(chat_id, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{chat_id}/{user_id}")
    private ResponseEntity<Void> removeUserFromChat(@Min(1) @PathVariable Long chat_id, @Min(1) @PathVariable Long user_id, Principal principal){
        groupService.removeUserFromChat(chat_id, user_id, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/message/{chat_id}")
    private ResponseEntity<Void> sendFile(Principal principal, @Min(1) @PathVariable Long chat_id, @RequestParam(value = "file") MultipartFile file){
        groupService.sendFile(principal, chat_id, file);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/message/{message_id}")
    private ResponseEntity<?> getFile(Principal principal, @Min(1) @PathVariable Long message_id){
        return groupService.getFile(principal, message_id);
    }

    @PostMapping("/add/{chat_id}/{user_id}")
    private ResponseEntity<Void> addUserToChat(Principal principal, @Min(1) @PathVariable Long chat_id, @Min(1) @PathVariable Long user_id){
        groupService.addUserToChat(principal, chat_id, user_id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/picture/{chat_id}")
    private ResponseEntity<Void> setChatPicture(Principal principal, @Min(1) @PathVariable Long chat_id, @RequestParam(value = "file") MultipartFile file){
        groupService.setChatPicture(principal, chat_id, file);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/leave")
    private ResponseEntity<Void> leaveChat(@Min(1) @RequestParam Long chat_id, Principal principal){
        groupService.leaveChat(chat_id, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
