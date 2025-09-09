package jame.dev.dtos;

import jame.dev.models.enums.EGenre;
import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import lombok.Builder;

@Builder
public record CopyDetailsDto(
        Integer idCopy,
        int copyNum,
        String bookName,
        EGenre genre,
        EStatusCopy status,
        ELanguage language) {
}
