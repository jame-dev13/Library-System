package jame.dev.utils;

import jame.dev.connection.ConnectionDB;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Utility class to perform Data Manipulation Language(DML):
 * <b>INSERT, UPDATE y DELETE</b>.
 *
 * <p>This class offers static methods witch allows to execute SQL queries
 * of a simplify way using {@link PreparedStatement}, avoid duplication code
 * and guarantee the resources close.</p>
 *
 * <p>The main stream consist of prepare the query, set the params,
 * execute the query and manage the transaction with commit or rollback as the case may be</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 *  String sql = "INSERT INTO users(name, username) VALUES(?, ?)";
 *  DMLActions.insert(sql, "Juan Pérez", "juan@example.com");
 * }</pre>
 *
 * <p><b>Note:</b> The class depends on {@code ConnectionDB} to get the connection to the DB.</p>
 *
 * @author jame-dev13
 */
@Log
public final class DMLActions {

   /**
    * Return a {@link BiConsumer} encargado de asignar valores a un
    * {@link PreparedStatement} en base a los parámetros proporcionados.
    *
    * @return un {@link BiConsumer} que recibe el PreparedStatement y un arreglo de parámetros.
    */
   private static final ConnectionDB connectionDB = ConnectionDB.getInstance();

   private static BiConsumer<PreparedStatement, Object[]> setParams() {
      return (ps, params) -> {
         Optional.ofNullable(params)
                 .ifPresentOrElse(objects -> {
                    for (int i = 0; i < objects.length; i++) {
                       try {
                          ps.setObject((i + 1), objects[i]);
                       } catch (SQLException e) {
                          throw new RuntimeException("Error setting params at index: " + (i + 1) + ": " + e);
                       }
                    }
                 }, () -> log.severe("Params are null."));
      };
   }

   /**
    * Ejecuta una sentencia DML (INSERT, UPDATE o DELETE).
    *
    * <p>Este método obtiene una conexión de {@link ConnectionDB},
    * prepara la sentencia con los parámetros recibidos, la ejecuta
    * y realiza commit si fue exitosa. En caso de error se hace rollback.</p>
    *
    * @param sql    sentencia SQL a ejecutar (no debe ser nula).
    * @param params parámetros opcionales para la sentencia SQL.
    * @throws SQLException     si ocurre un error al ejecutar la sentencia o gestionar la conexión.
    * @throws RuntimeException si falla el establecimiento de parámetros o la ejecución.
    */
   private static void execute(String sql, Object... params) throws SQLException {
      Connection connection = connectionDB.getConnection();
      Optional.ofNullable(connection).orElseThrow(SQLException::new);
      Optional.ofNullable(sql).orElseThrow(NullPointerException::new);
      try {
         try (PreparedStatement st = connection.prepareStatement(sql)) {
            setParams().accept(st, params);
            int rows = st.executeUpdate();
            log.info((rows > 0) ? rows + " rows affected at ." + " \n" : "0 rows affected.\n");
         }
         connection.commit();
      } catch (SQLException e) {
         connection.rollback();
         throw new RuntimeException("Connection Error \n", e);
      } finally {
         connection.setAutoCommit(true);
         connection.close();
      }
   }

   /**
    * Ejecuta una sentencia {@code INSERT} en la base de datos.
    *
    * @param sql    sentencia SQL de inserción.
    * @param params parámetros opcionales a insertar.
    * @throws SQLException si ocurre un error durante la operación.
    */
   public static void insert(String sql, Object... params) throws SQLException {
      execute(sql, params);
   }

   /**
    * Ejecuta una sentencia {@code UPDATE} en la base de datos.
    *
    * @param sql    sentencia SQL de actualización.
    * @param params parámetros opcionales a actualizar.
    * @throws SQLException si ocurre un error durante la operación.
    */
   public static void update(String sql, Object... params) throws SQLException {
      execute(sql, params);
   }

   /**
    * Ejecuta una sentencia {@code DELETE} en la base de datos.
    *
    * @param sql    sentencia SQL de eliminación.
    * @param params parámetros opcionales para la eliminación.
    * @throws SQLException si ocurre un error durante la operación.
    */
   public static void delete(String sql, Object... params) throws SQLException {
      execute(sql, params);
   }
}
