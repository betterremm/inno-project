package com.innowise.userservice.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UserWithCardsResponse(
        Long id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<PaymentCardResponse> cards
) {
}
