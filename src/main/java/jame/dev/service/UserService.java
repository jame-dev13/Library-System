package jame.dev.service;

import jame.dev.models.entitys.UserEntity;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.utils.DMLActions;
import jame.dev.utils.DQLActions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService implements CRUDRepo<UserEntity> {
    @Override
    public List<UserEntity> getAll() {
        String sql = """
                SELECT * FROM users;
                """;
        return DQLActions.select(sql, rs ->
                UserEntity.builder()
                        .id(rs.getInt(1))
                        .name(rs.getString(2))
                        .email(rs.getString(3))
                        .password(rs.getString(4))
                        .role(ERole.valueOf(rs.getString(4)))
                        .token(rs.getString(5))
                        .verified(rs.getBoolean(6))
                        .build()
                );
    }

    @Override
    public void save(UserEntity user) {
        String sql = """
                INSERT INTO users (name, email, password, role, token, verified)
                VALUES (?,?,?,?,?,?)
                """;
        Object[] params = {
            user.getName(), user.getEmail(), user.getPassword(),
            user.getRole().name(), user.getToken(), user.isVerified()
        };
        try {
            DMLActions.insert(sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        UserEntity result = DQLActions.selectWhere(sql, rs->
                UserEntity.builder()
                        .id(rs.getInt(1))
                        .name(rs.getString(2))
                        .email(rs.getString(3))
                        .password(rs.getString(4))
                        .role(ERole.valueOf(rs.getString(5)))
                        .token(rs.getString(6))
                        .verified(rs.getBoolean(6))
                        .build(),
                id).getFirst();
        return Optional.of(result);
    }

    @Override
    public void updateById(UserEntity t) {
        String sql = """
                UPDATE users SET name = ?, email = ?, password = ?, role = ?, verified = ?
                WHERE id = ?
                """;
        Object[] params = {
                t.getName(), t.getEmail(), t.getPassword(), t.getRole().name(),
                t.isVerified(), t.getId()
        };
        try {
            DMLActions.update(sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try {
            DMLActions.delete(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
