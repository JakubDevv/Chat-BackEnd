package org.example.chatapp.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.example.chatapp.dto.tokens.RefreshToken;
import org.example.chatapp.dto.tokens.Tokens;
import org.example.chatapp.dto.user.UserAuthRequest;
import org.example.chatapp.dto.user.UserCreateDTO;
import org.example.chatapp.dto.user.UserCreatedDTO;
import org.example.chatapp.services.AuthServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    public AuthController(AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<UserCreatedDTO> createNewUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        return new ResponseEntity<>(authServiceImpl.saveUser(userCreateDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Tokens> authenticate(@Valid @RequestBody UserAuthRequest userAuthRequest) {
        return new ResponseEntity<>(authServiceImpl.authenticate(userAuthRequest), HttpStatus.OK);
    }

    @PostMapping("/access-token")
    public ResponseEntity<Tokens> getNewAccessToken(@Valid @RequestBody RefreshToken refreshToken){
        return new ResponseEntity<>(authServiceImpl.getAccessToken(refreshToken), HttpStatus.CREATED);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(){
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping("/photo")
    public ResponseEntity<Void> setUserPhoto(@Valid @Min(1) @RequestParam Long userId, @RequestParam(value = "file") MultipartFile file){
        authServiceImpl.setUserPhoto(userId, file);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
