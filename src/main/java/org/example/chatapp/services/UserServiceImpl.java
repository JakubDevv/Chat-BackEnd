package org.example.chatapp.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.chatapp.aop.UpdateActivity;
import org.example.chatapp.dto.InterestCreateDTO;
import org.example.chatapp.dto.InterestDTO;
import org.example.chatapp.dto.InterestStatsDTO;
import org.example.chatapp.dto.MessageAmountDTO;
import org.example.chatapp.dto.friends.*;
import org.example.chatapp.dto.user.UserDTO;
import org.example.chatapp.dto.user.UserImageDTO;
import org.example.chatapp.dto.user.UserNotificationDTO;
import org.example.chatapp.dto.user.UserShortDTO;
import org.example.chatapp.exception.user.UserNotFoundException;
import org.example.chatapp.mapper.UserMapper;
import org.example.chatapp.model.Interest;
import org.example.chatapp.model.User;
import org.example.chatapp.model.enums.FriendStatus;
import org.example.chatapp.repository.FriendRepository;
import org.example.chatapp.repository.GroupUserRepository;
import org.example.chatapp.repository.InterestRepository;
import org.example.chatapp.repository.UserRepository;
import org.example.chatapp.s3.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService{

    @Value("${bucket.name}")
    private String bucketName;

    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final FriendRepository friendRepository;
    private final UserMapper userMapper;
    private final GroupUserRepository groupUserRepository;
    private final S3Service s3Service;

    public UserServiceImpl(UserRepository userRepository, InterestRepository interestRepository, FriendRepository friendRepository, UserMapper userMapper, GroupUserRepository groupUserRepository, S3Service s3Service) {
        this.userRepository = userRepository;
        this.interestRepository = interestRepository;
        this.friendRepository = friendRepository;
        this.userMapper = userMapper;
        this.groupUserRepository = groupUserRepository;
        this.s3Service = s3Service;
    }

    @UpdateActivity
    @Override
    public List<InterestDTO> getInterestsByUserId(Principal principal) {
        return interestRepository.findAllByUser_Username(principal.getName());
    }

    @UpdateActivity
    @Override
    public void deleteInterestsById(Long id, Principal principal) {
        interestRepository.deleteById(id);
    }

    @UpdateActivity
    @Override
    public Long addInterests(InterestCreateDTO interestCreateDTO, Principal principal) {
        User userByUsername = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        Interest interest = new Interest(interestCreateDTO.value());
        interest.setUser(userByUsername);
        Interest save = interestRepository.save(interest);
        return save.getId();
    }

    @UpdateActivity
    @Override
    public UserDTO getPersonalData(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return new UserDTO(user.getId(), user.getFirst_name(), user.getLast_name(), user.getEmail(), user.getCountry(), user.getCity(), groupUserRepository.getUnreadMessagesByUser(user));
    }

    @UpdateActivity
    @Override
    public List<FriendDTO> getUsers(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        List<FriendDTO> friends = new ArrayList<>();
        List<Long> pending = friendRepository.getPending(user.getId());
        List<Long> toAccept = friendRepository.getToAccept(user.getId());
        List<Long> friendss = friendRepository.getFriends1(user.getId());
        List<Long> friendss2 = friendRepository.getFriends2(user.getId());
        List<Long> users = friendRepository.getUsers(user.getId());
        List<Long> nonFriends = users.stream()
                .filter(userId -> !pending.contains(userId) && !toAccept.contains(userId) && !friendss.contains(userId) && !friendss2.contains(userId) && !Objects.equals(userId, user.getId()))
                .toList();
        friends.addAll(pending.stream().map(userr -> userMapper.mapUserToFriendDTO(userr, FriendStatus.PENDING, user.getId())).toList());
        friends.addAll(toAccept.stream().map(userr -> userMapper.mapUserToFriendDTO(userr, FriendStatus.TO_ACCEPT, user.getId())).toList());
        friends.addAll(friendss.stream().map(userr -> userMapper.mapUserToFriendDTO(userr, FriendStatus.ACCEPTED, user.getId())).toList());
        friends.addAll(friendss2.stream().map(userr -> userMapper.mapUserToFriendDTO(userr, FriendStatus.ACCEPTED, user.getId())).toList());
        friends.addAll(nonFriends.stream().map(userr -> userMapper.mapUserToFriendDTO(userr, null, user.getId())).toList());
        return friends;
    }

    @UpdateActivity
    @Override
    public void changeBackgroundColor(String bgColor, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        user.setBgcolor(bgColor);
        userRepository.save(user);
    }

    @UpdateActivity
    @Override
    public List<InterestStatsDTO> getFriendsInterests(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return friendRepository.calculateFriendInterestStats(user.getId());
    }

    @Override
    public UserImageDTO getImage(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return new UserImageDTO(user.getId(), user.getBgcolor());
    }

    @Override
    public List<FriendOnlineDTO> getActiveFriends(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return friendRepository.getOnlineFriends(user.getId(), LocalDateTime.now().minusMinutes(2)).stream().map(userr -> {
            User user1 = userRepository.findById(userr).orElseThrow(() -> new UserNotFoundException(principal.getName()));
            return new FriendOnlineDTO(user1.getId(), user1.getBgcolor(), user1.getFirst_name());
        }).toList();
    }

    @Override
    public FriendsOnlineDTO getActiveFriendsCount(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return new FriendsOnlineDTO(friendRepository.getOnlineFriendsCount(user.getId(), LocalDateTime.now().minusMinutes(2)), friendRepository.getAllFriends(user.getId()));
    }

    @Override
    public List<UserShortDTO> getNewUsers(Principal principal) {
        User user1 = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "created"));
        return userRepository.findAll(pageable).filter(user -> !Objects.equals(user.getId(), user1.getId())).stream().map(user ->{
            FriendStatus usersStatus = friendRepository.getUsersStatus(user1.getId(), user.getId());
            return new UserShortDTO(user.getId(), user.getFirst_name(), user.getLast_name(), usersStatus, user.getCreated(), friendRepository.countCommonFriends(user1.getId(), user.getId()));
        }).toList();
    }

    @Override
    public List<UserShortDTO> getUsersByMutualFriends(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        List<UserShortDTO> list = new ArrayList<>(userRepository.findAll().stream().filter(userr -> !Objects.equals(userr.getId(), user.getId())).map(userr -> {
            return new UserShortDTO(userr.getId(), userr.getFirst_name(), userr.getLast_name(), friendRepository.getUsersStatus(user.getId(), userr.getId()), userr.getCreated(), friendRepository.countCommonFriends(user.getId(), userr.getId()));
        }).toList());
        list.sort((a,b) -> Long.compare(b.mutualFriends(), a.mutualFriends()));
        return list.stream().limit(6).toList();
    }

    @Override
    public List<FriendsCountriesDTO> getFriendsCountries(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        List<User> friendsCountries = friendRepository.getFriendsCountries(user.getId());
        Map<String, Long> countryCountMap = new HashMap<>();

        for (User friend : friendsCountries) {
            countryCountMap.merge(friend.getCountry(), 1L, Long::sum);
        }

        List<FriendsCountriesDTO> combinedResults = new ArrayList<>();
        for (Map.Entry<String, Long> entry : countryCountMap.entrySet()) {
            combinedResults.add(new FriendsCountriesDTO(entry.getKey(), entry.getValue()));
        }

        return combinedResults;
    }

    @Override
    public List<MessageAmountDTO> getSentMessagesByTime(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return friendRepository.getSentMessagesByTime(user.getId());
    }

    @Override
    public List<UserNotificationDTO> getNotifications(Principal principal) {
        return userRepository.getNotifications(principal.getName());
    }

    @Transactional
    @Override
    public void deleteNotificationById(Principal principal, Long id) {
        userRepository.deleteNotificationById(id);
    }

    @Override
    public ResponseEntity<?> getUserProfilePictureById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        try {
            byte[] image = s3Service.getObject(
                    bucketName,
                    "Chat/User" + user.getId()
            );
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(image);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<FriendShortDTO> getFriends(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return friendRepository.getFriends(user.getId()).stream().map(userr -> userMapper.mapUserToFriendShortDTO(userr, user.getId())).toList();
    }

    @Override
    public void updateUserProfilePicture(Principal principal, MultipartFile file) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        try {
            s3Service.putObject(
                    bucketName,
                    "Chat/User" + user.getId(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }
}
