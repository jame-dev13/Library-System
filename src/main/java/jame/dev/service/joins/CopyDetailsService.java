package jame.dev.service.joins;

import jame.dev.dtos.copies.CopyDetailsDto;
import jame.dev.models.enums.EGenre;
import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import jame.dev.repositorys.Joinable;
import jame.dev.utils.db.DQLActions;

import java.util.List;
import java.util.UUID;

/**
 * Service class that implement the contract defined on {@link Joinable} interface
 * to get the data set for a prepared query with table joins.
 */
public final class CopyDetailsService implements Joinable<CopyDetailsDto> {
   /**
    * Passes a sql query to be performed for the class {@link DQLActions}{@code .select(String sql, ResultMapper rs)}
    * @return A List witch contains objects of CopyDetailsDto data.
    */
   @Override
   public List<CopyDetailsDto> getJoins() {
      final String sql = """
              SELECT c.id AS ID, c.uuid AS UUID,
              c.copy_num AS COPY_N,
              b.title AS TITLE, b.genre AS GENRE,
              c.status AS STATUS, c.language AS LANGUAGE
              FROM copies c INNER JOIN
              books b ON b.id = c.id_book
              WHERE c.copy_num > 1 AND c.borrowed = 0
              """;
      return DQLActions.select(sql, rs ->
              CopyDetailsDto.builder()
                      .uuid(UUID.fromString(rs.getString("UUID")))
                      .idCopy(rs.getInt("ID"))
                      .copyNum(rs.getInt("COPY_N"))
                      .bookName(rs.getString("TITLE"))
                      .genre(EGenre.valueOf(rs.getString("GENRE")))
                      .status(EStatusCopy.valueOf(rs.getString("STATUS")))
                      .language(ELanguage.valueOf(rs.getString("LANGUAGE")))
                      .build()
      );
   }
}
