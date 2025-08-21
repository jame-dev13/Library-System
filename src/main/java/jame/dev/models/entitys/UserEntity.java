package jame.dev.models.entitys;


import jame.dev.models.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
    private Integer id;
    private UUID uuid;
    private String name;
    private String email;
    private String password;
    private ERole role;
    private String token;
    private boolean verified;
}
