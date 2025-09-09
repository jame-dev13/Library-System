package jame.dev.service;

import jame.dev.dtos.CopyDetailsDto;
import jame.dev.models.entitys.CopyEntity;
import jame.dev.models.enums.EGenre;
import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.repositorys.IMultiQuery;
import jame.dev.utils.DMLActions;
import jame.dev.utils.DQLActions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CopyService implements CRUDRepo<CopyEntity>, IMultiQuery<CopyDetailsDto>
{

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
                    .statusCopy(EStatusCopy.valueOf(rs.getString(5)))
                    .language(ELanguage.valueOf(rs.getString(6)))
                    .build()
            );
    }

    @Override
    public void save(CopyEntity copyEntity) {
        String sql = """
                INSERT INTO copies (uuid, id_book, copy_num, status, language) VALUES (?,?,?,?,?);
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
                                .statusCopy(EStatusCopy.valueOf(rs.getString(4)))
                                .language(ELanguage.valueOf(rs.getString(5)))
                                .build()
                , uuid.toString());
        return Optional.ofNullable(result.getFirst());
    }

    @Override
    public void update(CopyEntity t) {
        String sql = """
                UPDATE copies SET status = ? WHERE uuid = ?
                """;
        try {
            DMLActions.update(sql, t.getStatusCopy().name(), t.getUuid().toString());
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

   @Override
   public List<CopyDetailsDto> getJoinsAll() {
       String sql = """
               SELECT c.id AS ID, c.copy_num AS COPY_N,
               b.title AS TITLE, b.genre AS GENRE,
               c.status AS STATUS, c.language AS LANGUAGE
               FROM copies c INNER JOIN
               books b ON b.id = c.id_book
               WHERE c.copy_num > 1
               """;
          return DQLActions.select(sql, rs ->
                  CopyDetailsDto.builder()
                          .idCopy(rs.getInt("ID"))
                          .copyNum(rs.getInt("COPY_N"))
                          .bookName(rs.getString("TITLE"))
                          .genre(EGenre.valueOf(rs.getString("GENRE")))
                          .status(EStatusCopy.valueOf(rs.getString("STATUS")))
                          .language(ELanguage.valueOf(rs.getString("LANGUAGE")))
                          .build()
          );
   }
}
