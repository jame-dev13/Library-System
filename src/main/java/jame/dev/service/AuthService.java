package jame.dev.service;

import jame.dev.dtos.users.SessionDto;
import jame.dev.dtos.users.UserDto;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.IAuthRepo;
import jame.dev.utils.DQLActions;
import org.mindrot.jbcrypt.BCrypt;

import java.util.NoSuchElementException;
import java.util.UUID;

public final class AuthService implements IAuthRepo {
   @Override
   public SessionDto signIn(UserDto user) {
      final String SQL_VERIFICATION = """
              SELECT username, password
              FROM users
              WHERE username = ? AND verified = 1
              """;

      final String SQL_SESSION = """
              SELECT id, uuid, username, role
              FROM users
              WHERE username = ?
              """;

      try {
         UserDto userDb = DQLActions.selectWhere(
                 SQL_VERIFICATION,
                 rs -> UserDto.builder()
                         .username(rs.getString("username"))
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
                         .uuid(UUID.fromString(rs.getString("uuid")))
                         .username(rs.getString("username"))
                         .role(ERole.valueOf(rs.getString("role")))
                         .build(),
                 user.username()
         ).getFirst();

      } catch (NoSuchElementException e) {
         return null;
      }
   }

}
