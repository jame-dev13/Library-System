package jame.dev.controller;

import jame.dev.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.io.IOException;

public class Admin {

    @FXML
    private Tab tabUsers;

    @FXML
    private Tab tabBooks;

    @FXML
    private Tab tabLoans;

    @FXML
    private Tab tabFines;

    @FXML
    public void initialize() throws IOException {
        Parent users = FXMLLoader.load(Main.class.getResource("/templates/adminPanes/Users.fxml"));
        tabUsers.setContent(users);

        Parent books = FXMLLoader.load(Main.class.getResource("/templates/adminPanes/Books.fxml"));
        tabBooks.setContent(books);

        Parent loans = FXMLLoader.load(Main.class.getResource("/templates/adminPanes/Loans.fxml"));
        tabLoans.setContent(loans);

        Parent fines = FXMLLoader.load(Main.class.getResource("/templates/adminPanes/Fines.fxml"));
        tabFines.setContent(fines);
    }
}
