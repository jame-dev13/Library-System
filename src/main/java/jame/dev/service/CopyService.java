package jame.dev.service;

import jame.dev.dtos.CopyDto;
import jame.dev.models.entitys.CopyEntity;
import jame.dev.models.enums.EStatusCopy;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.utils.DMLActions;
import jame.dev.utils.DQLActions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CopyService implements CRUDRepo<CopyEntity> {
    @Override
    public List getAll() {
        String sql = """
                SELECT b.id AS id_book, b.title AS title, b.author AS author, b.ISBN AS ISBN,
                b.language AS lang, b.genre AS genre, c.status AS status
                FROM copies c INNER JOIN books b ON
                b.id = c.id_book
                """;
        return DQLActions.select(sql, rs ->
            CopyDto.builder()
                    .id(rs.getInt("id_book"))
                    .title(rs.getString("title"))
                    .author(rs.getString("author"))
                    .ISBN(rs.getString("ISBN"))
                    .language(rs.getString("language"))
                    .genre(rs.getString("genre"))
                    .status(EStatusCopy.valueOf(rs.getString("status")))
                    .build()
            );
    }

    @Override
    public void save(CopyEntity copyEntity) {
        String sql = """
                INSERT INTO copies (id_book, status) VALUES (?,?);
                """;
        Object[] params = {
                copyEntity.getIdBook(), copyEntity.getStatusCopy().name()
        };
        try {
            DMLActions.insert(sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CopyEntity> findById(Integer id) {
        String sql = """
                SELECT * FROM copies WHERE id = ?
                """;
        List<CopyEntity> result = DQLActions.selectWhere(sql, rs ->
                CopyEntity.builder()
                        .id(rs.getInt("id"))
                        .idBook(rs.getInt("id_book"))
                        .statusCopy(EStatusCopy.valueOf(rs.getString("status")))
                        .build()
                , id);
        return Optional.of(result.getFirst());
    }

    @Override
    public void updateById(CopyEntity t) {
        String sql = """
                UPDATE copies SET status = ? WHERE id = ?
                """;
        try {
            DMLActions.update(sql, t.getStatusCopy().name(), t.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM copies WHERE id = ?";
        try {
            DMLActions.delete(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
