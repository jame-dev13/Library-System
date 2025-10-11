package jame.dev.dtos.users;

import lombok.Builder;

/**
 * Builds an immutable object input dto for with the user credentials.
 * @param username
 * @param password
 */
@Builder
public record UserDto (String username, String password){
}
