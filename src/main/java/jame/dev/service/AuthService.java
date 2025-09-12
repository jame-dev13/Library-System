package jame.dev.service;

import jame.dev.dtos.SessionDto;
import jame.dev.dtos.UserDto;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.IAuthRepo;
import jame.dev.utils.DQLActions;
import org.mindrot.jbcrypt.BCrypt;

import java.util.NoSuchElementException;

public final class AuthService implements IAuthRepo {
   @Override
   public SessionDto signIn(UserDto user) {
      final String SQL_VERIFICATION = """
            SELECT email, password
            FROM users
            WHERE email = ? AND verified = 1
            """;

      final String SQL_SESSION = """
            SELECT id, email, role
            FROM users
            WHERE email = ?
            """;

      try {
         UserDto userDb = DQLActions.selectWhere(
                 SQL_VERIFICATION,
                 rs -> UserDto.builder()
                         .username(rs.getString("email"))
                         .password(rs.getString("password"))
                         .build(),
                 user.username()
         ).getFirst();

         boolean validPassword = BCrypt.checkpw(user.password(), userDb.password());
         if (!validPassword) {
            return null;
         }

         return DQLActions.selectWhere(
                 SQL_SESSION,
                 rs -> SessionDto.builder()
                         .id(rs.getInt("id"))
                         .email(rs.getString("email"))
                         .role(ERole.valueOf(rs.getString("role")))
                         .build(),
                 user.username()
         ).getFirst();

      } catch (NoSuchElementException e) {
         return null;
      }
   }

}
