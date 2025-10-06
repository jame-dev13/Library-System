package jame.dev.utils;

import jame.dev.dtos.users.InfoUserDto;
import jame.dev.models.entitys.UserEntity;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.UserService;

import java.util.UUID;

public class InitAdminUtil {
   private static final CRUDRepo<UserEntity> REPO = new UserService();
   private static final String SQL = """
           SELECT username FROM users
           WHERE username = ?
           """;

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
