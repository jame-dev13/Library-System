package jame.dev.dtos.copies;

import jame.dev.models.enums.EGenre;
import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import lombok.Builder;

import java.util.UUID;

/**
 * Builds an immutable object to represent a CopyDetailsDto witch is built by
 * a combinations of {@link jame.dev.models.entitys.BookEntity} object and
 * {@link jame.dev.models.entitys.CopyEntity} object properties.
 * @param uuid
 * @param idCopy
 * @param copyNum
 * @param bookName
 * @param genre
 * @param status
 * @param language
 */
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
