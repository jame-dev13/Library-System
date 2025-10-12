package jame.dev.utils.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper interface to get Objects depending on its implementations.
 * @param <T>
 */
public interface ResultMapper<T> {
   /**
    * Maps the ResultSet entry into an Object.
    * @param t the ResultSet
    * @return a mapped object.
    * @throws SQLException if there's a problem while gets the ResultSet.
    */
    T map(ResultSet t) throws SQLException;
}
