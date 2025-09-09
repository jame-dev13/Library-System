package jame.dev.dtos;

import jame.dev.models.enums.ERole;
import lombok.Builder;

@Builder
public record SessionDto(Integer id, String email, ERole role) {
}
