package jame.dev.service;

import jame.dev.dtos.loans.LoanDetailsDto;
import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.repositorys.IMultiQuery;
import jame.dev.utils.DMLActions;
import jame.dev.utils.DQLActions;
import jame.dev.utils.SessionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class LoanService implements CRUDRepo<LoanEntity>, IMultiQuery<LoanDetailsDto> {

   @Override
   public List<LoanEntity> getAll() {
      String sql = "SELECT * FROM loans";
      return DQLActions.select(sql, rs ->
              LoanEntity.builder()
                      .id(rs.getInt(1))
                      .uuid(UUID.fromString(rs.getString(2)))
                      .idUser(rs.getInt(3))
                      .idCopy(rs.getInt(4))
                      .statusLoan(EStatusLoan.valueOf(rs.getString(5)))
                      .loanDate(rs.getDate(6).toLocalDate())
                      .returnDate(rs.getDate(7).toLocalDate())
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
              loanEntity.getUuid().toString(), loanEntity.getIdUser(),
              loanEntity.getIdCopy(), loanEntity.getStatusLoan().name(),
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
      LoanEntity result = DQLActions.selectWhere(sql, rs ->
                      LoanEntity.builder()
                              .id(rs.getInt(1))
                              .uuid(UUID.fromString(rs.getString(2)))
                              .idUser(rs.getInt(3))
                              .idCopy(rs.getInt(4))
                              .statusLoan(EStatusLoan.valueOf(rs.getString(5)))
                              .loanDate(rs.getDate(6).toLocalDate())
                              .returnDate(rs.getDate(7).toLocalDate())
                              .build()
              , uuid.toString()).getFirst();
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
              loanEntity.getReturnDate(), loanEntity.getUuid().toString()
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
         DMLActions.delete(sql, uuid.toString());
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public List<LoanDetailsDto> getJoinsAll() {
      final String sql = """
              SELECT l.uuid AS UUID, b.title AS TITLE, b.author AS AUTHOR,
              l.status AS STATUS,
              DATEDIFF(l.date_expiration, CURDATE()) AS REMAINING_DAYS
              FROM books b
              INNER JOIN copies c ON c.id_book = b.id
              INNER JOIN loans l ON l.id_copy = c.id
              INNER JOIN users u ON u.id = l.id_user
              WHERE u.id = ?
              """;
      Integer id = SessionManager.getInstance().getSessionDto().id();
      return DQLActions.selectWhere(sql, rs ->
              LoanDetailsDto.builder()
                      .uuid(UUID.fromString(rs.getString("UUID")))
                      .title(rs.getString("TITLE"))
                      .author(rs.getString("AUTHOR"))
                      .statusLoan(EStatusLoan.valueOf(rs.getString("STATUS")))
                      .remainingDays(rs.getInt("REMAINING_DAYS"))
                      .build(), id
      );
   }
}
