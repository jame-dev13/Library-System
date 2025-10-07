package jame.dev.service.joins;

import jame.dev.dtos.fines.FineDetailsDto;
import jame.dev.repositorys.Joinable;
import jame.dev.utils.DQLActions;

import java.util.List;
import java.util.UUID;

public class FineDetailsService implements Joinable<FineDetailsDto> {
   @Override
   public List<FineDetailsDto> getJoins() {
      final String sql = """
              SELECT f.uuid AS UUID,
              u.name AS NAME, f.id_user AS ID_USER,
              f.cause AS CAUSE,
              DATEDIFF(f.expiration, CURRENT_DATE) AS DAYS_REM
              FROM fines f
              INNER JOIN users u
              ON u.id = f.id_user
              """;
      return DQLActions.select(sql, rs -> FineDetailsDto.builder()
              .uuid(UUID.fromString(rs.getString("UUID")))
              .nameUser(rs.getString("NAME"))
              .idUser(rs.getInt("ID_USER"))
              .cause(rs.getString("CAUSE"))
              .daysRemaining(rs.getInt("DAYS_REM"))
              .build());
   }
}
