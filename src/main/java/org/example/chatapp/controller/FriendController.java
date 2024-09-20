package org.example.chatapp.controller;

import jakarta.validation.constraints.Min;
import org.example.chatapp.model.Notification;
import org.example.chatapp.model.User;
import org.example.chatapp.model.enums.NotificationType;
import org.example.chatapp.repository.NotificationRepository;
import org.example.chatapp.repository.UserRepository;
import org.example.chatapp.services.FriendService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/friend")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFriend(Principal principal, @Min(1) @RequestParam Long userId){
        friendService.removeFriend(principal, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<Void> createFriendRequest(Principal principal, @Min(1) @RequestParam Long userId){
        friendService.createFriendRequest(principal, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Void> acceptFriendRequest(Principal principal, @Min(1) @RequestParam Long userId){
        friendService.acceptFriendRequest(principal, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
