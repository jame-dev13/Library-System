package jame.dev.utils;

import jame.dev.connection.ConnectionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;


/**
 * This class provides methods for doing database queries like SELECT.
 */
public class DQLActions {

    public static <T> List<T> select(String sql, ResultMapper<T> mapper) {
        Objects.requireNonNull(sql);
        List<T> result = new ArrayList<>();
        try(Connection connection = ConnectionDB.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                result.add(mapper.map(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Connection Failed: " + e.getMessage(), e);
        }
        return result;
    }

    public static <T> List<T> selectWhere(String sql,
                                          ResultMapper<T> mapper,
                                          Object... params){
        Objects.requireNonNull(sql);
        List<T> result = new ArrayList<>();
        try(Connection connection = ConnectionDB.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){
            setParams().accept(ps, params);
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapper.map(rs));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Connection Failed: " + e.getMessage(), e);
        }
        return result;
    }

    private static BiConsumer<PreparedStatement, Object[]> setParams(){
        return (ps, params) ->{
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
    }
}
