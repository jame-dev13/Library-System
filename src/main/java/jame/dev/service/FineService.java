package jame.dev.service;

import jame.dev.models.entitys.FineEntity;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.utils.DMLActions;
import jame.dev.utils.DQLActions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FineService implements CRUDRepo<FineEntity> {
    @Override
    public List<FineEntity> getAll() {
        String sql = "SELECT * FROM fines";
        return DQLActions.select(sql, rs->
                FineEntity.builder()
                        .id(rs.getInt(1))
                        .idUser(rs.getInt(2))
                        .cause(rs.getString(3))
                        .expiration(rs.getDate(4))
                        .build());
    }

    @Override
    public void save(FineEntity fineEntity) {
        String sql = """
                INSERT INTO fines (id_user, cause, expiration) VALUES (?,?,?);
                """;
        Object[] params = {
                fineEntity.getIdUser(), fineEntity.getCause(), fineEntity.getExpiration()
        };

        try {
            DMLActions.insert(sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FineEntity> findById(Integer id) {
        String sql = "SELECT * FROM fines WHERE id = ?";
        FineEntity result =  DQLActions.selectWhere(sql, rs ->
                FineEntity.builder()
                        .id(rs.getInt(1))
                        .idUser(rs.getInt(2))
                        .cause(rs.getString(3))
                        .expiration(rs.getDate(4))
                        .build()
                ,id).getFirst();
        return Optional.of(result);
    }

    @Override
    public void updateById(FineEntity t) {
        String sql = """
                UPDATE fines SET id_user = ?, cause = ?, expiration = ? WHERE id = ?
                """;
        Object[] params = {t.getIdUser(), t.getCause(), t.getExpiration(), t.getId()};
        try {
            DMLActions.update(sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM fines WHERE id = ?";
        try {
            DMLActions.delete(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
