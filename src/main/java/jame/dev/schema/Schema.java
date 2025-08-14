package jame.dev.schema;

import jame.dev.connection.ConnectionDB;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Log
public class Schema implements IDoSchema {

    public Schema(){
        init();
    }

    private void init(){
        Map<String, String> tables = new LinkedHashMap<>();
        tables.put("users", this.queryUsers());
        tables.put("books", this.queryBooks());
        tables.put("fines", this.queryFines());
        tables.put("copies", this.queryCopies());
        tables.put("loans", this.queryLoans());
        tables.put("history_loans", this.queryHistoryLoans());
        tables.forEach(this::createTable);
    }

    @Override
    public void createTable(String name, String query) {
        try(Connection connection = ConnectionDB.getInstance().getConnection();
            PreparedStatement st = connection.prepareStatement(query)
        ){
            st.execute();
            log.info("Table %s created.\n".formatted(name));
        }catch (SQLException e){
            log.severe("Error trying to connect with the db. " + e.getMessage());
        }
    }

    private String queryUsers() {
        return """
            CREATE TABLE IF NOT EXISTS users (
                id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                name VARCHAR(60) NOT NULL,
                email VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                role CHAR(10) NOT NULL,
                token VARCHAR(8) UNIQUE NOT NULL,
                verified TINYINT(1) NOT NULL
            );
            """;
    }

    private String queryBooks(){
        return """
            CREATE TABLE IF NOT EXISTS books (
                id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                title VARCHAR(80) NOT NULL,
                editorial VARCHAR(15) NOT NULL,
                ISBN CHAR(13) UNIQUE NOT NULL,
                publication_date DATE NOT NULL,
                pages SMALLINT NOT NULL,
                genre VARCHAR(20) NOT NULL,
                language CHAR(3) NOT NULL
            );
            """;
    }

    private String queryFines(){
        return """
            CREATE TABLE IF NOT EXISTS fines(
                id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
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

    private String queryCopies(){
        return """
            CREATE TABLE IF NOT EXISTS copies(
                id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                id_book INT NOT NULL,
                copy_num INT NOT NULL,
                status CHAR(10) NOT NULL,
                CONSTRAINT fk_copies_book FOREIGN KEY (id_book)
                REFERENCES books(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE
            );
            """;
    }

    private String queryLoans(){
        return """
            CREATE TABLE IF NOT EXISTS loans(
                id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
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
                REFERENCES copies(n_copy)
                ON DELETE CASCADE
                ON UPDATE CASCADE
            );
            """;
    }

    private String queryHistoryLoans(){
        return """
            CREATE TABLE IF NOT EXISTS history_loans(
                id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                id_loan INT NOT NULL,
                CONSTRAINT fk_history_loan FOREIGN KEY (id_loan)
                REFERENCES loans(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE
            );
            """;
    }
}
