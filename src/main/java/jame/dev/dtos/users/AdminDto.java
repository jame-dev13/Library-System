package jame.dev.dtos.users;

import jame.dev.models.enums.ERole;
import lombok.Builder;

import java.util.UUID;

/**
 * Builds an immutable object to represent the data for those users
 * that has the role of {@link ERole}.ADMIN, to prevent that their properties
 * gonna been modified.
 * @param id
 * @param uuid
 * @param name
 * @param email
 * @param username
 * @param role
 */
@Builder
public record AdminDto(
        Integer id,
        UUID uuid,
        String name,
        String email,
        String username,
        ERole role
) {
}
