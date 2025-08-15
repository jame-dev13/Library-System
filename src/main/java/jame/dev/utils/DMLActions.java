package jame.dev.utils;

import jame.dev.connection.ConnectionDB;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

@Log
public class DMLActions {

    private static final BiConsumer<PreparedStatement, Object[]> setParams =
            (ps, params) -> {
                if(params != null){
                    for (int i = 0; i < params.length; i++) {
                        try {
                            ps.setObject(i + 1, params[i]);
                        } catch (SQLException e) {
                            throw new RuntimeException("Error setting parameter at index: " + (i+1) + ':', e);
                        }
                    }
                }
            };

    private static void execute(String sql, Object... params) throws SQLException{
        Objects.requireNonNull(sql, "query can´t be null!");
        Connection connection = null;
        try {
            connection = ConnectionDB.getInstance().getConnection();
            try (PreparedStatement st = connection.prepareStatement(sql)) {
                setParams.accept(st, params);
                int rows = st.executeUpdate();
                log.info((rows > 0) ? rows + " affected.\n" : "0 rows affected.\n");
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException("Connection failed. \n", e);
        }finally {
            if(connection != null){
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    public static void insert(String sql, Object... params) throws SQLException {
        execute(sql, params);
    }

    public static void update(String sql, Object... params) throws SQLException {
        execute(sql, params);
    }

    public static <T> List<T> select(String sql, ResultMapper<T> mapper) {
        return getList(sql, mapper);
    }

    public static <T> List<T> selectWhere(String sql, ResultMapper<T> mapper, Object... params){
        return getList(sql, mapper, params);
    }

    public static void delete(String sql, Object... params) throws SQLException {
        execute(sql, params);
    }

    private static <T> List<T> getList(String sql, ResultMapper<T> mapper){
        Objects.requireNonNull(sql);
        List<T> result = new ArrayList<>();
        try(Connection connection = ConnectionDB.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    result.add(mapper.map(rs));
                }
            }

        }catch (SQLException e){
            throw new RuntimeException("Connection Failed: " + e.getMessage(), e);
        }
        return result;
    }

    private static <T> List<T> getList(String sql, ResultMapper<T> mapper, Object... params){
        Objects.requireNonNull(sql);
        List<T> result = new ArrayList<>();
        try(Connection connection = ConnectionDB.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){
            setParams.accept(ps, params);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    result.add(mapper.map(rs));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Connection Failed: " + e.getMessage(), e);
        }
        return result;
    }

}
