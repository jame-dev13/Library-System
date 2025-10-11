package jame.dev.schema;

import jame.dev.connection.ConnectionDB;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Defines The creation of the SQl Table Scheme for this application.
 */
@Log
public final class Schema {

   private final ConnectionDB dbInstance = ConnectionDB.getInstance();
   /**
    * Build the schema
    */
   public Schema() {
      init();
   }

   /**
    * Executes the creation of tables in order with his query
    */
   private void init() {
      Map<String, String> tables = new LinkedHashMap<>();
      tables.put("users", this.queryUsers());
      tables.put("books", this.queryBooks());
      tables.put("fines", this.queryFines());
      tables.put("copies", this.queryCopies());
      tables.put("loans", this.queryLoans());
      tables.put("history_loans", this.queryHistoryLoans());
      tables.put("drop_trigger", this.queryDropTriggerIfExists());
      tables.put("trigger_after_insert_loan", this.queryTriggerHistoryLoans());
      tables.forEach(this::createTable);
   }

   /**
    * Creates any table in sql.
    * Given a name and a query to be performed, it does a null check for both
    * throwing a supplier exception that is a {@link java.util.NoSuchElementException} if no one
    * is specified.
    * tries the connection with the db, prepares the statement with the query and then executes it.
    * @param name the db table name.
    * @param query the sql query.
    */
   private void createTable(String name, String query) {
      Optional.ofNullable(name).orElseThrow();
      Optional.ofNullable(query).orElseThrow();
      try (Connection connection = dbInstance.getConnection();
           PreparedStatement st = connection.prepareStatement(query)) {
         st.execute();
         log.info("Table %s created.\n".formatted(name));
      } catch (SQLException e) {
         log.severe("Can't create. " + name + ": " + e.getMessage());
      }
   }

   /**
    * Query to build the table users.
    *
    * @return an sql query
    */
   private String queryUsers() {
      return """
              CREATE TABLE IF NOT EXISTS users (
                  id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                  uuid CHAR(36) NOT NULL,
                  name VARCHAR(60) NOT NULL,
                  email VARCHAR(50) UNIQUE NOT NULL,
                  username VARCHAR(50) UNIQUE NOT NULL,
                  password VARCHAR(255) NOT NULL,
                  role CHAR(10) NOT NULL,
                  token VARCHAR(8) UNIQUE NOT NULL,
                  verified TINYINT(1) NOT NULL
              );
              """;
   }

   /**
    * Query to build the table books.
    *
    * @return an sql query
    */
   private String queryBooks() {
      return """
              CREATE TABLE IF NOT EXISTS books (
                  id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                  uuid CHAR(36) NOT NULL,
                  title VARCHAR(80) NOT NULL,
                  author VARCHAR(80) NOT NULL,
                  editorial VARCHAR(50) NOT NULL,
                  ISBN CHAR(13) UNIQUE NOT NULL,
                  publication_date DATE NOT NULL,
                  pages SMALLINT NOT NULL,
                  genre VARCHAR(50) NOT NULL
              );
              """;
   }

   /**
    * Query to build the table fines.
    *
    * @return an sql query
    */
   private String queryFines() {
      return """
              CREATE TABLE IF NOT EXISTS fines(
                  id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                  uuid CHAR(36) NOT NULL,
                  id_user INT NOT NULL,
                  cause VARCHAR(60) NOT NULL,
                  expiration DATE NOT NULL,
                  CONSTRAINT fk_fines_user FOREIGN KEY (id_user)
                  REFERENCES users(id)
                  ON DELETE CASCADE
                  ON UPDATE CASCADE
              );
              """;
   }

   /**
    * Query to build the table copies.
    *
    * @return an sql query
    */
   private String queryCopies() {
      return """
              CREATE TABLE IF NOT EXISTS copies(
                  id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                  uuid CHAR(36) NOT NULL,
                  id_book INT NOT NULL,
                  copy_num INT NOT NULL,
                  borrowed TINYINT(1) DEFAULT 0,
                  status CHAR(10) NOT NULL,
                  language CHAR(3) NOT NULL,
                  CONSTRAINT fk_copies_book FOREIGN KEY (id_book)
                  REFERENCES books(id)
                  ON DELETE CASCADE
                  ON UPDATE CASCADE
              );
              """;
   }

   /**
    * Query to build the table loans.
    *
    * @return an sql query
    */
   private String queryLoans() {
      return """
              CREATE TABLE IF NOT EXISTS loans(
                  id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                  uuid CHAR(36) NOT NULL,
                  id_user INT NOT NULL,
                  id_copy INT NOT NULL,
                  status CHAR(10) NOT NULL,
                  date_loan DATE NOT NULL,
                  date_expiration DATE NOT NULL,
                  CONSTRAINT fk_loans_user FOREIGN KEY (id_user)
                  REFERENCES users(id)
                  ON DELETE CASCADE
                  ON UPDATE CASCADE,
                  CONSTRAINT fk_loans_copy FOREIGN KEY (id_copy)
                  REFERENCES copies(id)
                  ON DELETE CASCADE
                  ON UPDATE CASCADE
              );
              """;
   }

   /**
    * Query to build the table history_loans.
    *
    * @return an sql query
    */
   private String queryHistoryLoans() {
      return """
              CREATE TABLE IF NOT EXISTS history_loans(
                  id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                  id_loan INT NOT NULL,
                  action_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  CONSTRAINT fk_history_loan FOREIGN KEY (id_loan)
                  REFERENCES loans(id)
                  ON DELETE CASCADE
                  ON UPDATE CASCADE
              );
              """;
   }

   /**
    * Query to drop the Trigger if it exists.
    * @return a String sql query
    */
   private String queryDropTriggerIfExists(){
      return "DROP TRIGGER IF EXISTS after_insert_loan;";
   }

   /**
    * Query to create a Trigger on the db associated with the table
    * loans ah history_loans.
    * @return a String sql query.
    */
   private String queryTriggerHistoryLoans(){
      return """
              CREATE TRIGGER after_insert_loan
              AFTER INSERT ON loans
              FOR EACH ROW
              BEGIN
              INSERT INTO history_loans (id_loan)
              VALUES (NEW.id);
              END;
              """;
   }
}
