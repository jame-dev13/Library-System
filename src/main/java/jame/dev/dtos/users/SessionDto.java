package jame.dev.dtos.users;

import jame.dev.models.enums.ERole;
import lombok.Builder;

import java.util.UUID;

/**
 * Builds an immutable object dto to represent the data for a logged-in user.
 * @param id
 * @param uuid
 * @param username
 * @param role
 */
@Builder
public record SessionDto(
        Integer id,
        UUID uuid,
        String username,
        ERole role) {
}
