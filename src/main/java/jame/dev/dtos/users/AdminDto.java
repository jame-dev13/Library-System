package jame.dev.dtos.users;

import jame.dev.models.enums.ERole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AdminDto(
        Integer id, UUID uuid, String name, String email, String username, ERole role
) {
}
