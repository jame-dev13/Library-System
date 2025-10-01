package jame.dev.service;

import jame.dev.models.entitys.UserEntity;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.utils.DMLActions;
import jame.dev.utils.DQLActions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class UserService implements CRUDRepo<UserEntity> {
   @Override
   public List<UserEntity> getAll() {
      String sql = """
              SELECT * FROM users;
              """;
      return DQLActions.select(sql, rs ->
              UserEntity.builder()
                      .id(rs.getInt("id"))
                      .uuid(UUID.fromString(rs.getString("uuid")))
                      .name(rs.getString("name"))
                      .email(rs.getString("email"))
                      .username(rs.getString("username"))
                      .password(rs.getString("password"))
                      .role(ERole.valueOf(rs.getString("role")))
                      .token(rs.getString("token"))
                      .verified(rs.getBoolean("verified"))
                      .build()
      );
   }

   @Override
   public void save(UserEntity user) {
      String sql = """
              INSERT INTO users (uuid, name, email,username, password, role, token, verified)
              VALUES (?,?,?,?,?,?,?)
              """;
      Object[] params = {
              user.getUuid().toString(), user.getName(), user.getEmail(),
              user.getUsername(), user.getPassword(),
              user.getRole().name(), user.getToken(), user.isVerified()
      };
      try {
         DMLActions.insert(sql, params);
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Optional<UserEntity> findByUuid(UUID uuid) {
      String sql = "SELECT * FROM users WHERE uuid = ?";
      UserEntity result = DQLActions.selectWhere(sql, rs ->
                      UserEntity.builder()
                              .id(rs.getInt("id"))
                              .uuid(UUID.fromString(rs.getString("uuid")))
                              .name(rs.getString("name"))
                              .email(rs.getString("email"))
                              .username(rs.getString("username"))
                              .password(rs.getString("password"))
                              .role(ERole.valueOf(rs.getString("role")))
                              .token(rs.getString("token"))
                              .verified(rs.getBoolean("verified"))
                              .build(),
              uuid.toString()).getFirst();
      return Optional.ofNullable(result);
   }

   @Override
   public void update(UserEntity t) {
      String sql = """
              UPDATE users SET name = ?, email = ?, username = ?, password = ?, role = ?, verified = ?
              WHERE uuid = ?
              """;
      Object[] params = {
              t.getName(), t.getEmail(), t.getUsername(),
              t.getPassword(), t.getRole().name(),
              t.isVerified(), t.getUuid().toString()
      };
      try {
         DMLActions.update(sql, params);
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void deleteByUuid(UUID uuid) {
      String sql = "DELETE FROM users WHERE uuid = ?";
      try {
         DMLActions.delete(sql, uuid.toString());
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }
}
