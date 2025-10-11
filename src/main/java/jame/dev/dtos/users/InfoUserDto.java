package jame.dev.dtos.users;

import lombok.Builder;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Builds an immutable object input dto with a password property mutator in its creation.
 * @param name
 * @param email
 * @param username
 * @param password
 */
@Builder
public record InfoUserDto(String name, String email, String username, String password) {
   public InfoUserDto {
      password = BCrypt.hashpw(password, BCrypt.gensalt(12));
   }
}
