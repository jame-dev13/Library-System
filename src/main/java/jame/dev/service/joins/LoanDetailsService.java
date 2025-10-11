package jame.dev.service.joins;

import jame.dev.dtos.loans.LoanDetailsDto;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.Joinable;
import jame.dev.utils.db.DQLActions;
import jame.dev.utils.session.SessionManager;

import java.util.List;
import java.util.UUID;
/**
 * Service class that implement the contract defined on {@link Joinable} interface
 * to get the data set for a prepared query with table joins.
 */
public final class LoanDetailsService implements Joinable<LoanDetailsDto> {
   /**
    * Passes a sql query to be performed for the class {@link DQLActions}{@code .select(String sql, ResultMapper rs)}
    * @return A List witch contains objects of LoanDetailsDto data.
    */
   @Override
   public List<LoanDetailsDto> getJoins() {
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
