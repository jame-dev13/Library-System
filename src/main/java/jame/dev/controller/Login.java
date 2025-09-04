package jame.dev.controller;

import jame.dev.Main;
import jame.dev.dtos.UserDto;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.IAuthRepo;
import jame.dev.service.AuthService;
import jame.dev.utils.CustomAlert;
import jame.dev.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents a simple Login controller.
 */
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

   /**
    * Initializes components, global data and listeners, everything of that type
    * must be in this method.
    * @throws IOException
    */
    @FXML
    public void initialize() throws IOException {
        this.repo = new AuthService();
        btnLogin.setOnAction(this::handleClickLogin);
        btnToSignUp.setOnAction(this::handleClickGoToSignUp);
    }

   /**
    * Handles the authentications for a User and redirect it to a view depends on his role.
    * @param actionEvent the ActionEvent
    */
    @FXML
    private void handleClickLogin(ActionEvent actionEvent) {
        UserDto user = UserDto.builder()
                .username(txtUsername.getText())
                .password(txtPassword.getText())
                .role(ERole.UNDEFINED)
                .build();
        ERole role = this.repo.signIn(user);
        SessionManager session = SessionManager.getInstance();
        session.login(user.username());

        switch (role){
            case USER -> CustomAlert.getInstance()
                    .buildAlert(Alert.AlertType.INFORMATION, "INFO", "Login successfully.")
                       .showAndWait().ifPresent(confirmation -> {
                          if(confirmation == ButtonType.OK)
                             redirectTo(actionEvent, "/templates/userView.fxml");
                    });
           case ADMIN -> CustomAlert.getInstance()
                   .buildAlert(Alert.AlertType.INFORMATION, "INFO", "Login successfully.")
                   .showAndWait().ifPresent(confirmation -> {
                      if(confirmation == ButtonType.OK)
                         redirectTo(actionEvent, "/templates/adminView.fxml");
                   });
           default ->
               CustomAlert.getInstance()
                       .buildAlert(Alert.AlertType.ERROR, "ERROR", "Authentication failed.")
                       .showAndWait();
        }
    }

   /**
    * Handles the click for go to other view
    * @param actionEvent The ActionEvent
    */
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

   /**
    * Redirects to the scene if that exists.
    * @param event The ActionEvent
    * @param view The path of the view
    */
    @FXML private void redirectTo(ActionEvent event, String view) {
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
