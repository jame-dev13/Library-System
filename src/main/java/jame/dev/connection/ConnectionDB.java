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
public class ConnectionDB {

    private static volatile ConnectionDB instance;
    private final Dotenv dotenv = Dotenv.load();
    private final String URL = dotenv.get("DB_URL");
    private final String USER = dotenv.get("DB_USER");
    private final String PWD = dotenv.get("USER_PWD");
    private Connection connection;

    private ConnectionDB(){
        this.connection = this.connect();
    }

    public static ConnectionDB getInstance(){
        if(instance == null){
            synchronized (ConnectionDB.class){
                if(instance == null) instance = new ConnectionDB();
            }
        }
        log.info("Instance of class ConnectionDB: " + instance + '\n');
        return instance;
    }

   /**
    * Makes a connection to the DB using the credentials and turns off the
    * autocommit for be able to work with transactions.
    * @return a connection cursor for db operation.
    */
    private Connection connect(){
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PWD);
            connection.setAutoCommit(false);
            log.info("Connection Established\n");
            return connection;
        }catch (SQLException e){
            log.warning("Connection Failed\n");
            return null;
        }
    }


   /**
    * Gets the current connection if exists, is not closed or is valid, otherwise
    * it creates a new connection.
    * @return a SQL connection.
    */
    public Connection getConnection(){
        try{
            if(this.connection == null || this.connection.isClosed() || !this.connection.isValid(3)){
                return this.connect();
            }
            log.info("getConnection() executed!");
        }catch (SQLException e){
            log.warning("Error validating connection!\n");
            return null;
        }
        return this.connection;
    }

   /**
    * Closes the current instance of the class
    */
   public void close(){
        try{
            if(this.connection != null && !this.connection.isClosed()){
                this.connection.close();
                this.connection = null;
            }
        }catch (SQLException e){
            log.warning("Error trying to close the connection.\n");
        }
    }
}
