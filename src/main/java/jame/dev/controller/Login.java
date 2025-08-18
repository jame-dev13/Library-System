package jame.dev.controller;

import jame.dev.Main;
import jame.dev.dtos.UserDto;
import jame.dev.repositorys.IAuthRepo;
import jame.dev.service.AuthService;
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

import java.io.IOException;

public class Login{

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnToSignUp;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    private IAuthRepo repo;

    @FXML
    public void initialize() throws IOException {
        this.repo = new AuthService();
        btnLogin.setOnAction(this::handleClickLogin);
        btnToSignUp.setOnAction(this::handleClickGoToSignUp);
    }

    @FXML
    private void handleClickLogin(ActionEvent actionEvent) {
        UserDto user = UserDto.builder()
                .username(txtUsername.getText())
                .password(txtPassword.getText())
                .build();
        boolean sigIn = this.repo.signIn(user);
        if(sigIn) System.out.println("Credentials are valid.");
        else System.out.println("Bad credentials.");
    }

    @FXML
    private void handleClickGoToSignUp(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/templates/signUp.fxml"));
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
