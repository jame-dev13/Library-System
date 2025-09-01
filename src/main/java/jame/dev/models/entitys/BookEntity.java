package jame.dev.models.entitys;

import jame.dev.models.enums.ECategory;
import jame.dev.models.enums.ELanguage;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookEntity {
    private Integer id;
    @NonNull private UUID uuid;
    @NonNull private String title;
    @NonNull private String author;
    @NonNull private String editorial;
    @NonNull private ECategory category;
    @NonNull private String ISBN;
    @NonNull private LocalDate pubDate;
    @NonNull private int numPages;
    @NonNull private String genre;
    @NonNull private ELanguage language;
}
