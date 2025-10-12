package jame.dev.utils.db;

import jame.dev.repositorys.Joinable;
/**
 * Utility class to check the fines associated with a user.
 */
public final class CheckFinesUtil {

   /**
    * SELECT ID_USER WHERE ARE COINCIDENCES BETWEEN TABLES.
     */
   private static final String SQL = """
           SELECT f.id_user AS ID_USER
           FROM fines f
           INNER JOIN users u
           ON u.id = f.id_user
           """;

   /**
    * Uses the query {@value SQL} to do the check and then return if the result list contains the
    * param that we are passing like argument.
    * @param idUser the argument.
    * @return true if the argument is in the list, otherwise false.
    */
   public static boolean isFined(int idUser){
      Joinable<Integer> ids = () ->
         DQLActions.select(SQL, rs -> rs.getInt("ID_USER"));
      return ids.getJoins().contains(idUser);
   }
}
