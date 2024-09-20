package org.example.chatapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "interests")
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    @ManyToOne
    @JoinColumn(name = "fk_interests_users")
    private User user;

    public Interest() {
    }

    public Interest(String value) {
        this.value = value;
    }
}
