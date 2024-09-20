package org.example.chatapp.model.id;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.chatapp.model.Group;
import org.example.chatapp.model.User;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class GroupUserID implements Serializable {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user_id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group_id;

    public User getUser_id() {
        return user_id;
    }

    public Group getGroup_id() {
        return group_id;
    }

}
