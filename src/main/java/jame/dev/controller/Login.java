package jame.dev.controller;

import jame.dev.Main;
import jame.dev.dtos.UserDto;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.IAuthRepo;
import jame.dev.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

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
                .role(ERole.UNDEFINED)
                .build();
        ERole role = this.repo.signIn(user);

        switch (role){
            case USER -> redirectTo(actionEvent, "/templates/userView.fxml");
            case ADMIN -> redirectTo(actionEvent, "/templates/adminView.fxml");
            default -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Credentials");
                alert.setContentText("Bad Credentials!");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleClickGoToSignUp(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader
                    .load(Objects.requireNonNull(
                            Main.class.getResource("/templates/signUp.fxml")
                    ));
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void redirectTo(ActionEvent event, String view) {
        try{
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(view));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
