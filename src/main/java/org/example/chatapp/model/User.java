package org.example.chatapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users_")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String username;

    private String first_name;

    private String last_name;

    private String password;

    private String country;

    private String city;

    private LocalDateTime created;

    private String bgcolor;

    private LocalDateTime birth;

    private LocalDateTime last_activity;

    public User(String username, String first_name, String last_name, String password) {
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.birth = LocalDateTime.now();
        this.last_activity = LocalDateTime.now();
    }
}
