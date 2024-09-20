package org.example.chatapp.model.id;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import org.example.chatapp.model.User;

import java.io.Serializable;

@Data
@Embeddable
public class FriendID implements Serializable {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user_id;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend_id;

    public FriendID(){}

    public FriendID(User user_id, User friend_id) {
        this.user_id = user_id;
        this.friend_id = friend_id;
    }
}
