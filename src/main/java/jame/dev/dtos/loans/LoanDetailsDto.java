package jame.dev.dtos.loans;

import jame.dev.models.enums.EStatusLoan;
import lombok.Builder;

import java.util.UUID;

@Builder
public record LoanDetailsDto(
        UUID uuid, String title, String author, EStatusLoan statusLoan, int remainingDays
) {
}