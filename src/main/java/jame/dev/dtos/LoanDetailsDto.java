package jame.dev.dtos;

import jame.dev.models.enums.EStatusLoan;
import lombok.Builder;

import java.util.UUID;

@Builder
public record LoanDetailsDto(
        UUID uuid, String title, String author, EStatusLoan statusLoan, int remainingDays
) {
}