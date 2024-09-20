package org.example.chatapp.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.example.chatapp.dto.InterestCreateDTO;
import org.example.chatapp.dto.InterestDTO;
import org.example.chatapp.dto.InterestStatsDTO;
import org.example.chatapp.dto.MessageAmountDTO;
import org.example.chatapp.dto.friends.*;
import org.example.chatapp.dto.user.UserDTO;
import org.example.chatapp.dto.user.UserImageDTO;
import org.example.chatapp.dto.user.UserNotificationDTO;
import org.example.chatapp.dto.user.UserShortDTO;
import org.example.chatapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/personal-data")
    public ResponseEntity<UserDTO> getPersonalData(Principal principal) {
        return new ResponseEntity<>(userService.getPersonalData(principal), HttpStatus.OK);
    }

    @GetMapping("/image")
    public ResponseEntity<UserImageDTO> getImage(Principal principal) {
        return new ResponseEntity<>(userService.getImage(principal), HttpStatus.OK);
    }

    @GetMapping("/interests")
    public ResponseEntity<List<InterestDTO>> getInterestsByUser(Principal principal) {
        return new ResponseEntity<>(userService.getInterestsByUserId(principal), HttpStatus.OK);
    }

    @DeleteMapping("/interests")
    public ResponseEntity<Void> deleteInterestById(@Min(1) @RequestParam Long id, Principal principal) {
        userService.deleteInterestsById(id, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/interests")
    public ResponseEntity<Long> addInterest(@Valid @RequestBody InterestCreateDTO interestCreateDTO, Principal principal) {
        return new ResponseEntity<>(userService.addInterests(interestCreateDTO, principal), HttpStatus.CREATED);
    }

    @GetMapping("/friends/interests")
    public ResponseEntity<List<InterestStatsDTO>> getFriendsInterests(Principal principal) {
        return new ResponseEntity<>(userService.getFriendsInterests(principal), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<FriendDTO>> getUsers(Principal principal) {
        return new ResponseEntity<>(userService.getUsers(principal), HttpStatus.OK);
    }

    @GetMapping("/messages/amount")
    public ResponseEntity<List<MessageAmountDTO>> getSentMessagesByTime(Principal principal) {
        return new ResponseEntity<>(userService.getSentMessagesByTime(principal), HttpStatus.OK);
    }

    @PutMapping("/bg-color")
    public ResponseEntity<Void> changeBackgroundColor(@NotBlank @RequestParam String bgColor, Principal principal) {
        userService.changeBackgroundColor(bgColor, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/active-friends")
    public ResponseEntity<List<FriendOnlineDTO>> getActiveFriends(Principal principal) {
        return new ResponseEntity<>(userService.getActiveFriends(principal), HttpStatus.OK);
    }

    @GetMapping("/active-friends-count")
    public ResponseEntity<FriendsOnlineDTO> getActiveFriendsCount(Principal principal) {
        return new ResponseEntity<>(userService.getActiveFriendsCount(principal), HttpStatus.OK);
    }

    @GetMapping("/new")
    public ResponseEntity<List<UserShortDTO>> getNewUsers(Principal principal) {
        return new ResponseEntity<>(userService.getNewUsers(principal), HttpStatus.OK);
    }

    @GetMapping("/mutual-friends")
    public ResponseEntity<List<UserShortDTO>> getUsersByMutualFriends(Principal principal) {
        return new ResponseEntity<>(userService.getUsersByMutualFriends(principal), HttpStatus.OK);
    }

    @GetMapping("/countries")
    public ResponseEntity<List<FriendsCountriesDTO>> getFriendsCountries(Principal principal) {
        return new ResponseEntity<>(userService.getFriendsCountries(principal), HttpStatus.OK);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<UserNotificationDTO>> getNotifications(Principal principal) {
        return new ResponseEntity<>(userService.getNotifications(principal), HttpStatus.OK);
    }

    @DeleteMapping("/notification")
    public ResponseEntity<Void> deleteNotificationById(Principal principal, @Min(1) @RequestParam Long id) {
        userService.deleteNotificationById(principal, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/profile-picture/{id}")
    public ResponseEntity<?> getUserProfilePictureById(@Min(1) @PathVariable Long id) {
        return userService.getUserProfilePictureById(id);
    }

    @PostMapping("/profile-picture")
    public ResponseEntity<Void> updateUserProfilePicture(Principal principal, @RequestParam(value = "file") MultipartFile file) {
        userService.updateUserProfilePicture(principal, file);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<FriendShortDTO>> getFriends(Principal principal) {
        return new ResponseEntity<>(userService.getFriends(principal), HttpStatus.OK);
    }
}
