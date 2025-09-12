package jame.dev.service;

import jame.dev.models.entitys.FineEntity;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.utils.DMLActions;
import jame.dev.utils.DQLActions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class FineService implements CRUDRepo<FineEntity> {
    @Override
    public List<FineEntity> getAll() {
        String sql = "SELECT * FROM fines";
        return DQLActions.select(sql, rs->
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
        FineEntity result =  DQLActions.selectWhere(sql, rs ->
                        FineEntity.builder()
                                .id(rs.getInt(1))
                                .uuid(UUID.fromString(rs.getString(2)))
                                .idUser(rs.getInt(3))
                                .cause(rs.getString(4))
                                .expiration(rs.getDate(5).toLocalDate())
                                .build()
                ,uuid.toString()).getFirst();
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
}
