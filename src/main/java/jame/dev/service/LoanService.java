package jame.dev.service;

import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.utils.DMLActions;
import jame.dev.utils.DQLActions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LoanService implements CRUDRepo<LoanEntity> {


    @Override
    public List<LoanEntity> getAll() {
        String sql = "SELECT * FROM loans";
        return DQLActions.select(sql, rs ->
                LoanEntity.builder()
                        .id(rs.getInt(1))
                        .uuid(UUID.fromString(rs.getString(2)))
                        .idUser(rs.getInt(3))
                        .idCopy(rs.getInt(4))
                        .loanDate(rs.getDate(5).toLocalDate())
                        .returnDate(rs.getDate(6).toLocalDate())
                        .statusLoan(EStatusLoan.valueOf(rs.getString(7)))
                        .build());
    }

    @Override
    public void save(LoanEntity loanEntity) {
        String sql = """
                INSERT INTO loans (uuid, id_user, id_copy, status,
                date_loan, date_expiration)
                VALUES (?,?,?,?,?,?);
                """;
        Object[] params = {
                loanEntity.getUuid(), loanEntity.getIdUser(),
                loanEntity.getIdUser(), loanEntity.getStatusLoan().name(),
                loanEntity.getLoanDate(), loanEntity.getReturnDate()
        };
        try {
            DMLActions.insert(sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<LoanEntity> findByUuid(UUID uuid) {
        String sql = "SELECT * FROM loans WHERE uuid = ?";
        LoanEntity result = DQLActions.selectWhere(sql, rs->
                        LoanEntity.builder()
                                .id(rs.getInt(1))
                                .uuid(UUID.fromString(rs.getString(2)))
                                .idUser(rs.getInt(3))
                                .idCopy(rs.getInt(4))
                                .statusLoan(EStatusLoan.valueOf(rs.getString(5)))
                                .loanDate(rs.getDate(6).toLocalDate())
                                .returnDate(rs.getDate(7).toLocalDate())
                                .build()
                ,uuid).getFirst();
        return Optional.of(result);
    }

    @Override
    public void update(LoanEntity loanEntity) {
        String sql = """
                UPDATE loans SET id_user = ?, id_copy = ?, status = ?,
                date_loan = ?, date_expiration = ? WHERE uuid = ?
                """;
        Object[] params = {
                loanEntity.getIdUser(), loanEntity.getIdCopy(),
                loanEntity.getStatusLoan().name(), loanEntity.getLoanDate(),
                loanEntity.getReturnDate(), loanEntity.getUuid()
        };

        try {
            DMLActions.insert(sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        String sql = "DELETE FROM loans WHERE uuid = ?";
        try {
            DMLActions.delete(sql, uuid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
