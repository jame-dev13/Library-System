package jame.dev.service;

import jame.dev.dtos.fines.FineUserDto;
import jame.dev.models.entitys.FineEntity;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.repositorys.Joinable;
import jame.dev.utils.DMLActions;
import jame.dev.utils.DQLActions;
import jame.dev.utils.SessionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class FineService implements CRUDRepo<FineEntity>, Joinable<FineUserDto> {
   @Override
   public List<FineEntity> getAll() {
      String sql = "SELECT * FROM fines";
      return DQLActions.select(sql, rs ->
              FineEntity.builder()
                      .id(rs.getInt(1))
                      .uuid(UUID.fromString(rs.getString(2)))
                      .idUser(rs.getInt(3))
                      .cause(rs.getString(4))
                      .expiration(rs.getDate(5).toLocalDate())
                      .build());
   }

   @Override
   public void save(FineEntity fineEntity) {
      String sql = """
              INSERT INTO fines (uuid, id_user, cause, expiration) VALUES (?,?,?,?);
              """;
      Object[] params = {
              fineEntity.getUuid().toString(),
              fineEntity.getIdUser(),
              fineEntity.getCause(),
              fineEntity.getExpiration()
      };

      try {
         DMLActions.insert(sql, params);
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Optional<FineEntity> findByUuid(UUID uuid) {
      String sql = "SELECT * FROM fines WHERE uuid = ?";
      FineEntity result = DQLActions.selectWhere(sql, rs ->
                      FineEntity.builder()
                              .id(rs.getInt(1))
                              .uuid(UUID.fromString(rs.getString(2)))
                              .idUser(rs.getInt(3))
                              .cause(rs.getString(4))
                              .expiration(rs.getDate(5).toLocalDate())
                              .build()
              , uuid.toString()).getFirst();
      return Optional.of(result);
   }

   @Override
   public void update(FineEntity t) {
      String sql = """
              UPDATE fines SET id_user = ?, cause = ?, expiration = ? WHERE uuid = ?
              """;
      Object[] params = {t.getIdUser(), t.getCause(), t.getExpiration(), t.getUuid().toString()};
      try {
         DMLActions.update(sql, params);
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void deleteByUuid(UUID uuid) {
      String sql = "DELETE FROM fines WHERE uuid = ?";
      try {
         DMLActions.delete(sql, uuid.toString());
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public List<FineUserDto> getJoins() {
      final String sql = """
              SELECT f.cause AS CAUSE,
              f.expiration AS EXP,
              DATEDIFF(f.expiration, CURRENT_DATE) AS DAYS_DIFF
              FROM users u
              INNER JOIN fines f
              ON f.id_user = u.id
              WHERE f.id_user = ?
              """;
      int id = SessionManager.getInstance().getSessionDto().id();
      return DQLActions.selectWhere(sql, rs ->
                      FineUserDto.builder()
                              .cause(rs.getString("CAUSE"))
                              .expiration(rs.getDate("EXP").toLocalDate())
                              .daysRemaining(rs.getInt("DAYS_DIFF"))
                              .build(),
              id);
   }
}
