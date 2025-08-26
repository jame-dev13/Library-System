package jame.dev.models.entitys;

import jame.dev.models.enums.ELanguage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookEntity {
    private Integer id;
    private UUID uuid;
    private String title;
    private String author;
    private String editorial;
    private String ISBN;
    private LocalDate pubDate;
    private int numPages;
    private String genre;
    private ELanguage language;
}
