package jame.dev.connection;


import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This is a singleton class which provides methods to
 * connect to the database, get and close the current connection if exists
 * you must use getInstance to create an object of this class.
 */
@Log
public final class ConnectionDB {

   private static volatile ConnectionDB instance;
   private static final String URL;
   private static final String USER;
   private static final String PWD;

   static {
      Dotenv dotenv = Dotenv.load();
      URL = dotenv.get("DB_URL");
      USER = dotenv.get("DB_USER");
      PWD = dotenv.get("DB_PWD");
   }

   private ConnectionDB(){}

   public static ConnectionDB getInstance() {
      if (instance == null) {
         synchronized (ConnectionDB.class) {
            if (instance == null) instance = new ConnectionDB();
         }
      }
      return instance;
   }

   /**
    * Gets the current connection if exists, is not closed or is valid, otherwise
    * it creates a new connection.
    *
    * @return a SQL connection.
    */
   public Connection getConnection() {
      try {
         Connection con = DriverManager.getConnection(URL, USER, PWD);
         con.setAutoCommit(false);
         return con;
      } catch (SQLException e) {
         log.severe("Connection failed. " + e);
         return null;
      }
   }
}
