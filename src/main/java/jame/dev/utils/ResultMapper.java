package jame.dev.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
public interface ResultMapper<T> {
    T map(ResultSet t) throws SQLException;
}
