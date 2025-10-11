package jame.dev.dtos.loans;

import jame.dev.models.enums.EStatusLoan;
import lombok.Builder;

import java.util.UUID;

/**
 * Builds an immutable object composed by the join of data between books and loans,
 * it also calculates the property remainingDays.
 * @param uuid
 * @param title
 * @param author
 * @param statusLoan
 * @param remainingDays
 */
@Builder
public record LoanDetailsDto(
        UUID uuid,
        String title,
        String author,
        EStatusLoan statusLoan,
        int remainingDays
) {
}