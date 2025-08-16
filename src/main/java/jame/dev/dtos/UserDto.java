package jame.dev.dtos;

import lombok.Builder;

@Builder
public record UserDto (String username, String password){
}
