package jame.dev.models.entitys;

import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import lombok.*;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CopyEntity {
    private Integer id;
    @NonNull private UUID uuid;
    @NonNull private int idBook;
    @NonNull private int copyNum;
    private Boolean borrowed;
    @NonNull private EStatusCopy statusCopy;
    @NonNull private ELanguage language;
}