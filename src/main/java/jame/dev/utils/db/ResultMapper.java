package jame.dev.utils.db;

import java.sql.ResultSet;
import java.sql.SQLException;
public interface ResultMapper<T> {
    T map(ResultSet t) throws SQLException;
}
