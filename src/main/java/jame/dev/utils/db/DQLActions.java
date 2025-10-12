package jame.dev.utils.db;

import jame.dev.connection.ConnectionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;


/**
 * This class provides methods for doing database queries like SELECT.
 * It uses a Connection Object provided by {@link ConnectionDB} class and a {@link PreparedStatement}
 * and then the result is getting for the use of {@link ResultSet}.
 * It uses try-with-resources for auto closeable.
 * This class depends on {@link ConnectionDB} class for perform its operations.
 * Also, this class methods implement a {@link ResultMapper} for map the results
 * of our {@code ResultSet} to ObjectEntity class in the case may be and uses a {@link BiConsumer} to
 * set the params if these are present.
 * <p>
 * The main stream consist in prepare the query, set the params if there are any, get the
 * ResultSet and then apply the mapper and add it to the List that is going to be returned.
 */
public final class DQLActions {
   /**
    * Evaluates that the {@code sql} argument don't be null, throwing NullPointerException Handled if it is.
    * If not, prepares the connection and statement and gets the ResultSet and while these
    * rs has next, the mapper maps the rs iterator value and the result list add to itself.
    * <p> Implementation example:
    * <pre>
    *       {@code
    *          List<User> getUsers(){
    *           String sql = """
    *               SELECT name, email FROM users;
    *           """;
    *           return DQL.select(sql, rs -> new UserEntity(rs.getString("name"), rs.getString("email")));
    *            }
    *       }
    *       <pre/>
    * <p/>
    * <p><b>Note: <b/>You can use Builder to map the entity's too.</p>
    * @param sql the nonNull sql SELECT sentence.
    * @param mapper the {@code ResultMapper<T> mapper}
    * @return {@code List<T> of T type with the mapped values on the sql sentence.}
    */
   public static <T> List<T> select(String sql, ResultMapper<T> mapper) {
      Optional.ofNullable(sql).orElseThrow(NullPointerException::new);
      List<T> result = new ArrayList<>();
      try (Connection connection = ConnectionDB.getInstance().getConnection();
           PreparedStatement ps = connection.prepareStatement(sql);
           ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            result.add(mapper.map(rs));
         }
      } catch (SQLException e) {
         throw new RuntimeException("Connection Failed: " + e.getMessage(), e);
      }
      return result;
   }


   /**
    * Evaluates that the {@code sql} argument don't be null, throwing NullPointerException Handled if it is.
    * If not, prepares the connection and statement sets the params if it has any, and gets the ResultSet and while these
    * rs has next, the mapper maps the rs iterator value and the result list add to itself.
    * <p> Implementation example:
    * <pre>
    *       {@code
    *          List<User> getUsers(){
    *           String sql = """
    *               SELECT name, email FROM users WHERE email = ?;
    *           """;
    *           return DQL.selectWhere(sql, rs -> new UserEntity(rs.getString("name"), rs.getString("email")), "user1@mail.com");
    *            }
    *       }
    *       <pre/>
    * <p/>
    * <p><b>Note: <b/>You can use Builder to map the entity's too.</p>
    * @param sql the nonNull sql SELECT sentence.
    * @param mapper the {@code ResultMapper<T> mapper}.
    * @param params the optional params to set.
    * @return {@code List<T> of T type with the mapped values on the sql sentence.}
    */
   public static <T> List<T> selectWhere(String sql,
                                         ResultMapper<T> mapper,
                                         Object... params) {
      Optional.ofNullable(sql).orElseThrow(NullPointerException::new);
      List<T> result = new ArrayList<>();
      try (Connection connection = ConnectionDB.getInstance().getConnection();
           PreparedStatement ps = connection.prepareStatement(sql)) {
         setParams().accept(ps, params);
         try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
               result.add(mapper.map(rs));
            }
         }
      } catch (SQLException e) {
         throw new RuntimeException("Connection Failed: " + e.getMessage(), e);
      }
      return result;
   }

   /**
    * Return a {@link BiConsumer} that is who's going to set the params for the
    * {@link PreparedStatement}.
    * @return un {@link BiConsumer} that is accepting a {@code PreparedStatement} and
    * a params array.
    */
   private static BiConsumer<PreparedStatement, Object[]> setParams() {
      return (ps, params) -> {
         if (params != null) {
            for (int i = 0; i < params.length; i++) {
               try {
                  ps.setObject(i + 1, params[i]);
               } catch (SQLException e) {
                  throw new RuntimeException("Error setting parameter at index: " + (i + 1) + ':', e);
               }
            }
         }
      };
   }
}
