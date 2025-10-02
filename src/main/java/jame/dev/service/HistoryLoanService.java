package jame.dev.service;

import jame.dev.models.enums.EGenre;
import jame.dev.repositorys.IMultiQuery;
import jame.dev.utils.DQLActions;
import jame.dev.utils.SessionManager;

import java.util.List;

public class HistoryLoanService implements IMultiQuery<EGenre> {
   @Override
   public List<EGenre> getJoinsAll() {
      final String sql = """
              SELECT b.genre AS GENRE
              FROM books b
              INNER JOIN copies c
              ON c.id_book = b.id
              INNER JOIN loans l
              ON l.id_copy = c.id
              INNER JOIN history_loans hl
              ON hl.id_loan = l.id
              INNER JOIN users u
              ON u.id = l.id_user
              WHERE u.id = ?
              """;
      int id = SessionManager.getInstance().getSessionDto().id();
      return DQLActions.selectWhere(sql,
              rs -> EGenre.valueOf(rs.getString("GENRE")), id);
   }
}
