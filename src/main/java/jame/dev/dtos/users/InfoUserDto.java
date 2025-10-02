package jame.dev.dtos.users;

import lombok.Builder;
import org.mindrot.jbcrypt.BCrypt;

@Builder
public record InfoUserDto(String name, String email, String username, String password) {
   public InfoUserDto {
      password = BCrypt.hashpw(password, BCrypt.gensalt(12));
   }
}
