package org.example.chatapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "groups")
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime created;

    private boolean isduo;

    public Group(String name, boolean isduo) {
        this.name = name;
        this.created = LocalDateTime.now();
        this.isduo = isduo;
    }
}
