package jame.dev.service;

import jame.dev.dtos.CopyDto;
import jame.dev.models.entitys.CopyEntity;
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

public class CopyService implements CRUDRepo<CopyEntity>, IMultiQuery<CopyDto> {

    @Override
    public List<CopyDto> getAllWithInfo() {
        String sql = """
                SELECT b.id, b.title, b.author, b.ISBN, c.status, b.language, b.genre
                FROM copies c
                INNER JOIN books b ON b.id = c.id_book
                """;
        return DQLActions.select(sql, rs ->
                CopyDto.builder()
                        .id(rs.getInt(1))
                        .title(rs.getString(2))
                        .author(rs.getString(3))
                        .ISBN(rs.getString(4))
                        .status(EStatusCopy.valueOf(rs.getString(5)))
                        .language(ELanguage.valueOf(rs.getString(6)))
                        .genre(rs.getString(7))
                        .build());
    }

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
                    .build()
            );
    }

    @Override
    public void save(CopyEntity copyEntity) {
        String sql = """
                INSERT INTO copies (uuid, id_book, copy_num, status) VALUES (?,?,?,?);
                """;
        Object[] params = {
                copyEntity.getUuid(),
                copyEntity.getIdBook(),
                copyEntity.getCopyNum(),
                copyEntity.getStatusCopy().name()
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
                                .id(rs.getInt("id"))
                                .uuid(UUID.fromString(rs.getString("uuid")))
                                .idBook(rs.getInt("id_book"))
                                .statusCopy(EStatusCopy.valueOf(rs.getString("status")))
                                .build()
                , uuid);
        return Optional.of(result.getFirst());
    }

    @Override
    public void update(CopyEntity t) {
        String sql = """
                UPDATE copies SET status = ? WHERE uuid = ?
                """;
        try {
            DMLActions.update(sql, t.getStatusCopy().name(), t.getUuid());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        String sql = "DELETE FROM copies WHERE uuid = ?";
        try {
            DMLActions.delete(sql, uuid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
