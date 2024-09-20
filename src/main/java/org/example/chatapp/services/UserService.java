package org.example.chatapp.services;

import org.example.chatapp.dto.InterestCreateDTO;
import org.example.chatapp.dto.InterestDTO;
import org.example.chatapp.dto.InterestStatsDTO;
import org.example.chatapp.dto.MessageAmountDTO;
import org.example.chatapp.dto.friends.*;
import org.example.chatapp.dto.user.UserDTO;
import org.example.chatapp.dto.user.UserImageDTO;
import org.example.chatapp.dto.user.UserNotificationDTO;
import org.example.chatapp.dto.user.UserShortDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface UserService {

    List<InterestDTO> getInterestsByUserId(Principal principal);

    void deleteInterestsById(Long id, Principal principal);

    Long addInterests(InterestCreateDTO interestCreateDTO, Principal principal);

    UserDTO getPersonalData(Principal principal);

    List<FriendDTO> getUsers(Principal principal);

    void changeBackgroundColor(String bgColor, Principal principal);

    List<InterestStatsDTO> getFriendsInterests(Principal principal);

    UserImageDTO getImage(Principal principal);

    List<FriendOnlineDTO> getActiveFriends(Principal principal);

    FriendsOnlineDTO getActiveFriendsCount(Principal principal);

    List<UserShortDTO> getNewUsers(Principal principal);

    List<UserShortDTO> getUsersByMutualFriends(Principal principal);

    List<FriendsCountriesDTO> getFriendsCountries(Principal principal);

    List<MessageAmountDTO> getSentMessagesByTime(Principal principal);

    List<UserNotificationDTO> getNotifications(Principal principal);

    void deleteNotificationById(Principal principal, Long id);

    ResponseEntity<?> getUserProfilePictureById(Long id);

    List<FriendShortDTO> getFriends(Principal principal);

    void updateUserProfilePicture(Principal principal, MultipartFile file);
}
