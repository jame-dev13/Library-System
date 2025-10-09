package jame.dev.utils.db;

import jame.dev.repositorys.Joinable;

public final class CheckFinesUtil {

   private static final String SQL = """
           SELECT f.id_user AS ID_USER
           FROM fines f
           LEFT JOIN users u
           ON u.id = f.id_user
           """;
   public static boolean isFined(int idUser){
      Joinable<Integer> ids = () ->
         DQLActions.select(SQL, rs -> rs.getInt("ID_USER"));
      return ids.getJoins().contains(idUser);
   }
}
