package org.example.chatapp.services;

import java.security.Principal;

public interface FriendService {

    void removeFriend(Principal principal, Long userId);

    void acceptFriendRequest(Principal principal, Long userId);

    void createFriendRequest(Principal principal, Long userId);
}
