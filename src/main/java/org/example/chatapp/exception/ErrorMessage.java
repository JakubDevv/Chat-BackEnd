package org.example.chatapp.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorMessage(String error, String message, LocalDateTime timestamp) {
}
