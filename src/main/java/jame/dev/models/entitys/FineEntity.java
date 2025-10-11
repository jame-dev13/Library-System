package jame.dev.models.entitys;


import lombok.*;

import java.time.LocalDate;
import java.util.UUID;
/**
 * Entity class to perform DB operation for a Fine object with DAO.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FineEntity {
     private Integer id;
    @NonNull private UUID uuid;
    @NonNull private int idUser;
    @NonNull private String cause;
    @NonNull private LocalDate expiration;
}
