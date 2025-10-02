package jame.dev.dtos.fines;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record FineUserDto(String cause, LocalDate expiration, int daysRemaining) {
}
