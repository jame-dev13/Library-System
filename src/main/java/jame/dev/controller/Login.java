package jame.dev.controller;

import jame.dev.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.SneakyThrows;

public class Login{

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnToSignUp;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;



    @SneakyThrows
    @FXML
    public void initialize() {
        btnLogin.setOnAction(this::handleClickLogin);
        btnToSignUp.setOnAction(this::handleClickGoToSignUp);
    }

    @FXML
    private void handleClickLogin(ActionEvent actionEvent) {
        System.out.println("Login clicked");
    }

    @FXML
    private void handleClickGoToSignUp(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/templates/signUp.fxml"));
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
