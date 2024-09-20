package org.example.chatapp.services;


import org.example.chatapp.dto.tokens.RefreshToken;
import org.example.chatapp.dto.tokens.Tokens;
import org.example.chatapp.dto.user.UserAuthRequest;
import org.example.chatapp.dto.user.UserCreateDTO;
import org.example.chatapp.dto.user.UserCreatedDTO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {

    UserCreatedDTO saveUser(UserCreateDTO userCreateDTO);

    Tokens authenticate(UserAuthRequest userAuthRequest);

    Tokens getAccessToken(RefreshToken refreshToken);

    void setUserPhoto(Long userId, MultipartFile file);
}
