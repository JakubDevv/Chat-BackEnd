package org.example.chatapp.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorData<T>(String error, List<T> message, LocalDateTime timestamp) {
}
