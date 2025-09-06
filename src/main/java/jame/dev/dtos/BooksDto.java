package jame.dev.dtos;

import jame.dev.models.enums.EGenre;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

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
