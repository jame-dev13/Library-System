package jame.dev.service;

import jame.dev.dtos.users.SessionDto;
import jame.dev.dtos.users.UserDto;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.IAuthRepo;
import jame.dev.utils.db.DQLActions;
import org.mindrot.jbcrypt.BCrypt;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service Class that implements the contract defined in {@link IAuthRepo} to perform
 * a sign-in evaluating the data on the db table users.
 */
public final class AuthService implements IAuthRepo {
   /**
    * First builds an {@link UserDto} with the selection data of the given fields
    * and checks the password, if isn't valid then early returns null and sign-in attempt will fail.
    * If verification password pass, then retrieves the necessary data to builds a {@link SessionDto} object
    * that will be returned.
    * @param user {@link UserDto} object.
    * @return null or {@link SessionDto} depends on the case.
    */
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
