package jame.dev.service;

import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.utils.DMLActions;
import jame.dev.utils.DQLActions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class LoanService implements CRUDRepo<LoanEntity> {


    @Override
    public List<LoanEntity> getAll() {
        String sql = "SELECT * FROM loans";
        return DQLActions.select(sql, rs ->
                LoanEntity.builder()
                        .id(rs.getInt(1))
                        .idUser(rs.getInt(2))
                        .idCopy(rs.getInt(3))
                        .loanDate(rs.getDate(4))
                        .returnDate(rs.getDate(5))
                        .statusLoan(EStatusLoan.valueOf(rs.getString(6)))
                        .build());
    }

    @Override
    public void save(LoanEntity loanEntity) {
        String sql = """
                INSERT INTO loans (id_user, id_copy, status,
                date_loan, date_expiration)
                VALUES (?,?,?,?,?);
                """;
        Object[] params = {
                loanEntity.getIdUser(), loanEntity.getIdUser(), loanEntity.getStatusLoan().name(),
                loanEntity.getLoanDate(), loanEntity.getReturnDate()
        };
        try {
            DMLActions.insert(sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<LoanEntity> findById(Integer id) {
        String sql = "SELECT * FROM loans WHERE id = ?";
        LoanEntity result = DQLActions.selectWhere(sql, rs->
                LoanEntity.builder()
                        .id(rs.getInt(1))
                        .idUser(rs.getInt(2))
                        .idCopy(rs.getInt(3))
                        .statusLoan(EStatusLoan.valueOf(rs.getString(4)))
                        .loanDate(rs.getDate(5))
                        .returnDate(rs.getDate(6))
                        .build()
                ,id).getFirst();
        return Optional.of(result);
    }

    @Override
    public void updateById(LoanEntity loanEntity) {
        String sql = """
                UPDATE loans SET id_user = ?, id_copy = ?, status = ?,
                date_loan = ?, date_expiration = ? WHERE id = ?
                """;
        Object[] params = {
                loanEntity.getIdUser(), loanEntity.getIdCopy(),
                loanEntity.getStatusLoan().name(), loanEntity.getLoanDate(),
                loanEntity.getReturnDate(), loanEntity.getId()
        };

        try {
            DMLActions.insert(sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM loans WHERE id = ?";
        try {
            DMLActions.delete(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
