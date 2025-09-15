package jame.dev.unit;

import jame.dev.connection.ConnectionDB;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Connection DB tests")
public class ConnectionTests {

   private ConnectionDB connectionDB;
   private Connection connection;

   @BeforeEach
   void setUp(){
      this.connectionDB = ConnectionDB.getInstance();
      this.connection = this.connectionDB.getConnection();
   }

   @AfterEach
   void tearDown() throws SQLException {
      if(this.connection != null && !this.connection.isClosed())
         this.connection.close();
   }

   @Test
   @DisplayName("Connection valid")
   public void connectionValid() throws SQLException {
      assertTrue(this.connection.isValid(2), "The connection should be valid.");
   }

   @Test
   @DisplayName("Is Transactional")
   public void getTransactionalMode() throws SQLException {
      boolean autoCommit = this.connection.getAutoCommit();
      assertFalse(autoCommit, "The autocommit should be inactive.");
   }

   @Test
   @DisplayName("Connection close")
   public void closeConnection() throws SQLException {
      this.connection.close();
      assertTrue(this.connection.isClosed(), "The connection should be closed");
   }
}
