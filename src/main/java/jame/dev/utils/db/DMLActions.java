package jame.dev.utils.db;

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
 * <p><b>Note:</b> The class depends on {@link ConnectionDB} to get the connection to the DB.</p>
 *
 * @author jame-dev13
 */
@Log
public final class DMLActions {

   private static final ConnectionDB connectionDB = ConnectionDB.getInstance();

   /**
    * Return a {@link BiConsumer} that is who's going to set the params for the
    * {@link PreparedStatement}.
    * @return un {@link BiConsumer} that is accepting a {@code PreparedStatement} and
    * a params array.
    */
   private static BiConsumer<PreparedStatement, Object[]> setParams() {
      return (ps, params) ->
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

   }

   /**
    * Executes a DML sentence: (INSERT, UPDATE o DELETE).
    *
    * <p>This method gets the Connection, prepares the statement and
    * executes the query doing a commit on success or a rollback on failure.</p>
    *
    * @param sql query sentence (NonNull).
    * @param params Optional params.
    * @throws SQLException If the execution fail or can't get the connection with the db.
    * @throws RuntimeException if there's a fail during setting params or execution.
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
    * Executes the sentence {@code INSERT} on the databse.
    *
    * @param sql    INSERT Sentence.
    * @param params params to insert (optionals).
    * @throws SQLException if an error is present during the operation.
    */
   public static void insert(String sql, Object... params) throws SQLException {
      execute(sql, params);
   }

   /**
    * Executes the sentence {@code UPDATE} on the database.
    *
    * @param sql    UPDATE sentence.
    * @param params params to update(Optional).
    * @throws SQLException if an error is present during the operation.
    */
   public static void update(String sql, Object... params) throws SQLException {
      execute(sql, params);
   }

   /**
    * Executes the sentence {@code DELETE} on the database.
    *
    * @param sql    DELETE Sentence.
    * @param params params to delete, in this case there must be arguments, if not, it will throw
    an {@link java.util.NoSuchElementException}.
    * @throws SQLException if an error is present during the operation.
    */
   public static void delete(String sql, Object... params) throws SQLException {
      Optional.ofNullable(params).orElseThrow();
      execute(sql, params);
   }
}
