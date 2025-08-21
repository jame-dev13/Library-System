package jame.dev.models.entitys;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FineEntity {
    private Integer id;
    private UUID uuid;
    private int idUser;
    private String cause;
    private Date expiration;
}
