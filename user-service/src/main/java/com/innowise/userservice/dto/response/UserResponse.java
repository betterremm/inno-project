package com.innowise.userservice.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
