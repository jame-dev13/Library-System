package jame.dev.dtos.books;

import jame.dev.models.enums.EGenre;
import lombok.Builder;

@Builder
public record BorrowedBookDto(
        Integer id_book,
        EGenre genre
) {
}
