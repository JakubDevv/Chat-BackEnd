package org.example.chatapp.services;

import lombok.RequiredArgsConstructor;
import org.example.chatapp.aop.UpdateActivity;
import org.example.chatapp.dto.tokens.RefreshToken;
import org.example.chatapp.dto.tokens.Tokens;
import org.example.chatapp.dto.user.UserAuthRequest;
import org.example.chatapp.dto.user.UserCreateDTO;
import org.example.chatapp.dto.user.UserCreatedDTO;
import org.example.chatapp.jwt.JwtService;
import org.example.chatapp.model.User;
import org.example.chatapp.repository.UserRepository;
import org.example.chatapp.s3.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${bucket.name}")
    private String bucketName;

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final S3Service s3Service;

    public AuthServiceImpl(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, S3Service s3Service) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.s3Service = s3Service;
    }

    @Override
    public UserCreatedDTO saveUser(UserCreateDTO userCreateDTO) {
        User user = new User(userCreateDTO.userName(), userCreateDTO.firstName(), userCreateDTO.lastName(), passwordEncoder.encode(userCreateDTO.password()));
        User user1 = userRepository.save(user);
        return new UserCreatedDTO(user1.getId(), jwtService.generateRefreshToken(user1.getUsername()),  jwtService.generateAccessToken(user1.getUsername()));
    }

    @Override
    public Tokens authenticate(UserAuthRequest userAuthRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userAuthRequest.username(), userAuthRequest.password()));
        if (authenticate.isAuthenticated()) {
            return new Tokens(jwtService.generateRefreshToken(userAuthRequest.username()), jwtService.generateAccessToken(userAuthRequest.username()));
        } else {
            throw new RuntimeException("Wrong username or password");
        }
    }

    @UpdateActivity
    @Override
    public Tokens getAccessToken(RefreshToken refreshToken) {
        if(jwtService.validateRefreshToken(refreshToken.value())){
            return new Tokens(jwtService.generateRefreshToken(jwtService.getSubjectRefreshToken(refreshToken.value())), jwtService.generateAccessToken(jwtService.getSubjectRefreshToken(refreshToken.value())));
        }
        else {
            throw new IllegalArgumentException("Wrong refresh token");
        }
    }

    @Override
    public void setUserPhoto(Long userId, MultipartFile file) {
        try {
            s3Service.putObject(
                    bucketName,
                    "Chat/User" + userId,
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }
}
