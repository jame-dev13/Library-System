package jame.dev.models.entitys;


import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FineEntity {
    @NonNull private Integer id;
    @NonNull private UUID uuid;
    @NonNull private int idUser;
    @NonNull private String cause;
    @NonNull private LocalDate expiration;
}
