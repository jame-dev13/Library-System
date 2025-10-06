package jame.dev.dtos.copies;

import jame.dev.models.enums.EGenre;
import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CopyDetailsDto(
        UUID uuid,
        Integer idCopy,
        int copyNum,
        String bookName,
        EGenre genre,
        EStatusCopy status,
        ELanguage language) {
}
