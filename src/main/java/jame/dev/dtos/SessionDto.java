package jame.dev.dtos;

import jame.dev.models.enums.ERole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record SessionDto(Integer id, UUID uuid, String email, ERole role) {
}
