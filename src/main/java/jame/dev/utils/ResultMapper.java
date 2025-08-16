package jame.dev.utils;

import java.sql.SQLException;
@FunctionalInterface
public interface ResultMapper<T> {
    T map(T t) throws SQLException;
}
