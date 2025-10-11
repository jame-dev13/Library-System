package jame.dev.dtos.fines;

import lombok.Builder;

import java.time.LocalDate;

/**
 * Builds an immutable object with a calculated property.
 * @param cause
 * @param expiration
 * @param daysRemaining
 */
@Builder
public record FineUserDto(
        String cause,
        LocalDate expiration,
        int daysRemaining) {
}
