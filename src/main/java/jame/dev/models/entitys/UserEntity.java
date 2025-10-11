package jame.dev.models.entitys;


import jame.dev.models.enums.ERole;
import lombok.*;

import java.util.UUID;

/**
 * Entity class to perform DB operation for a User object with DAO.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
    private Integer id;
    @NonNull private UUID uuid;
    @NonNull private String name;
    @NonNull private String email;
    @NonNull private String username;
    @NonNull private String password;
    @NonNull private ERole role;
    @NonNull private String token;
    @NonNull private boolean verified;
}
