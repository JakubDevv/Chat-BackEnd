package org.example.chatapp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MessageAmountDTO(LocalDateTime date, Long amount) {
}
