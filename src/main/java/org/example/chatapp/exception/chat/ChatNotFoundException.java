package org.example.chatapp.exception.chat;

public class ChatNotFoundException extends RuntimeException {

    public ChatNotFoundException(Long id) {
        super(String.format(
                "Chat with id = %d not found in database",
                id
        ));
    }

}
