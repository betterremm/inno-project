package by.betterremm.userservice.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PaymentCardCreateRequest(
        @NotBlank
        @Pattern(regexp = "^\\d{4}-\\d{4}-\\d{4}-\\d{4}$")
        String number,
        @NotBlank @Size(max = 100) String holder,
        @Future LocalDate expirationDate
) {
}
