package jame.dev.utils.loader;

import jame.dev.dtos.users.InfoUserDto;
import jame.dev.models.entitys.UserEntity;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.UserService;
import jame.dev.utils.tools.TokenGenerator;
import jame.dev.utils.db.DQLActions;

import java.util.UUID;

/**
 * It initializes a {@code UserEntity} object with default properties and role of Admin.
 * <p><b>Note: </b>This class depends on the {@link CRUDRepo} and {@link UserService} implementation.</p>
 */
public final class InitAdminUtil {
   private static final CRUDRepo<UserEntity> REPO = new UserService();
   private static final String SQL = """
           SELECT username FROM users
           WHERE username = ?
           """;

   /**
    * Simply loads a user cataloged as Admin. If there is
    * a User into the db with this characters, then return outside the method.
    */
   public static void init() {
      InfoUserDto userDto = InfoUserDto.builder()
              .name("admin")
              .email("admin@mail.com")
              .username("admin123")
              .password("login")
              .build();
      UserEntity userEntity = UserEntity.builder()
              .uuid(UUID.randomUUID())
              .name(userDto.name())
              .email(userDto.email())
              .username(userDto.username())
              .password(userDto.password())
              .role(ERole.ADMIN)
              .token(TokenGenerator.genToken())
              .verified(true)
              .build();
      var coincidences = DQLActions.selectWhere(SQL,
              rs -> rs.getString("username"),
              userEntity.getUsername());
      if(!coincidences.isEmpty())
         return;
      REPO.save(userEntity);
   }
}
