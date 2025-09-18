package jame.dev.dtos;

import jame.dev.models.enums.EGenre;
import lombok.Builder;

@Builder
public record BorrowedBookDto(
        Integer id_book,
        EGenre genre
) {
}
