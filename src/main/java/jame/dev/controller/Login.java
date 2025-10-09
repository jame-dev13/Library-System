package jame.dev.controller;

import jame.dev.dtos.users.SessionDto;
import jame.dev.dtos.users.UserDto;
import jame.dev.repositorys.IAuthRepo;
import jame.dev.service.AuthService;
import jame.dev.utils.ComponentValidationUtil;
import jame.dev.utils.CustomAlert;
import jame.dev.utils.SessionManager;
import jame.dev.utils.ValidatorUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.Optional;

/**
 * Represents a simple Login controller.
 */
@Log
public class Login {

   @FXML
   private Label lbUsername;
   @FXML
   private Label lbPassword;
   @FXML
   private Button btnLogin;

   @FXML
   private Button btnToSignUp;

   @FXML
   private TextField txtUsername;

   @FXML
   private PasswordField txtPassword;

   private IAuthRepo repo;
   private static final CustomAlert alert = CustomAlert.getInstance();
   private static final SessionManager session = SessionManager.getInstance();

   /**
    * Initializes components, global data and listeners, everything of that type
    * must be in this method.
    */
   @FXML
   public void initialize() throws IOException {
      this.repo = new AuthService();
      btnLogin.setOnAction(this::handleClickLogin);
      btnToSignUp.setOnAction(this::handleClickGoToSignUp);
      ComponentValidationUtil.addValidation(txtUsername, lbUsername, ValidatorUtil::isValidString, "Username not valid.");
      ComponentValidationUtil.addValidation(txtPassword, lbPassword, ValidatorUtil::isValidPassword, "Password not valid");
   }

   /**
    * Handles the authentications for a User and redirect it to a view depends on his role.
    *
    * @param actionEvent the ActionEvent
    */
   @FXML
   private void handleClickLogin(ActionEvent actionEvent) {
      UserDto user = UserDto.builder()
              .username(txtUsername.getText().trim())
              .password(txtPassword.getText().trim())
              .build();
      SessionDto sessionDto = this.repo.signIn(user);
      Optional.ofNullable(sessionDto)
              .ifPresentOrElse(dto -> {
                 session.login(dto);
                 Platform.runLater(() ->
                         alert.buildAlert(Alert.AlertType.INFORMATION, "LOGIN", "Login attempt successfully")
                                 .show());
                 switch (dto.role()) {
                    case USER -> redirectTo(actionEvent, "/templates/userView.fxml");
                    case ADMIN -> redirectTo(actionEvent, "/templates/adminView.fxml");
                    default -> alert.buildAlert(Alert.AlertType.ERROR, "ERROR", "Login failed.").show();
                 }
              }, () -> alert.buildAlert(Alert.AlertType.WARNING, "WARNING", "Login attempt failed.").show());
   }

   /**
    * Handles the click for go to other view
    *
    * @param actionEvent The ActionEvent
    */
   @FXML
   private void handleClickGoToSignUp(ActionEvent actionEvent) {
      redirectTo(actionEvent, "/templates/signUp.fxml");
   }

   /**
    * Redirects to the scene if that exists.
    *
    * @param event The ActionEvent
    * @param view  The path of the view
    */
   @FXML
   private void redirectTo(ActionEvent event, String view) {
      try {
         FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
         Parent root = loader.load();
         Scene scene = new Scene(root);

         Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
         stage.setScene(scene);
         stage.show();
      } catch (IOException e) {
         Platform.runLater(() -> alert.buildAlert(Alert.AlertType.ERROR, "ERROR", "Redirection failed").show());
         log.severe("Resource loading went wrong: " + e);
      }
   }
}
