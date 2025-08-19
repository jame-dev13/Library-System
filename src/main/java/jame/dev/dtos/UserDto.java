package jame.dev.dtos;

import jame.dev.models.enums.ERole;
import lombok.Builder;

@Builder
public record UserDto (String username, String password, ERole role){
}
