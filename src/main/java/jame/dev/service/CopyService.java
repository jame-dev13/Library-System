package jame.dev.service;

import jame.dev.models.entitys.CopyEntity;
import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.utils.DMLActions;
import jame.dev.utils.DQLActions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class CopyService implements CRUDRepo<CopyEntity> {

   @Override
   public List<CopyEntity> getAll() {
      String sql = """
              SELECT * FROM copies;
              """;
      return DQLActions.select(sql, rs ->
              CopyEntity.builder()
                      .id(rs.getInt(1))
                      .uuid(UUID.fromString(rs.getString(2)))
                      .idBook(rs.getInt(3))
                      .copyNum(rs.getInt(4))
                      .borrowed(rs.getBoolean(5))
                      .statusCopy(EStatusCopy.valueOf(rs.getString(6)))
                      .language(ELanguage.valueOf(rs.getString(7)))
                      .build()
      );
   }

   @Override
   public void save(CopyEntity copyEntity) {
      String sql = """
              INSERT INTO copies
              (uuid, id_book, copy_num, status, language)
              VALUES (?,?,?,?,?);
              """;
      Object[] params = {
              copyEntity.getUuid().toString(),
              copyEntity.getIdBook(),
              copyEntity.getCopyNum(),
              copyEntity.getStatusCopy().name(),
              copyEntity.getLanguage().name()
      };
      try {
         DMLActions.insert(sql, params);
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Optional<CopyEntity> findByUuid(UUID uuid) {
      String sql = """
              SELECT * FROM copies WHERE uuid = ?
              """;
      List<CopyEntity> result = DQLActions.selectWhere(sql, rs ->
                      CopyEntity.builder()
                              .id(rs.getInt(1))
                              .uuid(UUID.fromString(rs.getString(2)))
                              .idBook(rs.getInt(3))
                              .copyNum(rs.getInt(4))
                              .borrowed(rs.getBoolean(5))
                              .statusCopy(EStatusCopy.valueOf(rs.getString(6)))
                              .language(ELanguage.valueOf(rs.getString(7)))
                              .build()
              , uuid.toString());
      return Optional.ofNullable(result.getFirst());
   }

   @Override
   public void update(CopyEntity t) {
      String sql = """
              UPDATE copies SET borrowed = ?, status = ?, language = ? WHERE uuid = ?
              """;
      try {
         Object[] params = {
                 t.getBorrowed(),
                 t.getStatusCopy().name(),
                 t.getLanguage().name(),
                 t.getUuid().toString()
         };
         DMLActions.update(sql, params);
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void deleteByUuid(UUID uuid) {
      String sql = "DELETE FROM copies WHERE uuid = ?";
      try {
         DMLActions.delete(sql, uuid.toString());
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }
}
