package jame.dev.dtos.books;

import jame.dev.models.enums.EGenre;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Builds an immutable object to represent a Book Data Transfer Object
 * @param id
 * @param uuid
 * @param title
 * @param author
 * @param editorial
 * @param ISBN
 * @param pubDate
 * @param numPages
 * @param genre
 */
@Builder
public record BooksDto(
        Integer id,
        UUID uuid,
        String title,
        String author,
        String editorial,
        String ISBN,
        LocalDate pubDate,
        int numPages,
        EGenre genre
) { }
