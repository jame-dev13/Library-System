package jame.dev.controller;

import jame.dev.dtos.InfoUserDto;
import jame.dev.models.entitys.UserEntity;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.UserService;
import jame.dev.utils.CustomAlert;
import jame.dev.utils.EmailSender;
import jame.dev.utils.TokenGenerator;
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
import java.util.UUID;

/**
 * Simple way to registration logic.
 */
@Log
public class SignUp {

   @FXML
   private TextField txtName;
   @FXML
   private TextField txtEmail;
   @FXML
   private TextField txtUsername;
   @FXML
   private PasswordField txtPassword;
   @FXML
   private Button btnReg;
   @FXML
   private Button btnToSignIn;

   private CRUDRepo<UserEntity> repo;
   private UserEntity user;
   private String token;
   private static final CustomAlert alert = CustomAlert.getInstance();

   /**
    * Initializes components, global data and listeners, everything of that type
    * must be in this method.
    *
    */
   @FXML
   private void initialize() throws IOException {
      this.repo = new UserService();
      btnReg.setOnAction(this::handleSignUp);
      btnToSignIn.setOnAction(this::handleReturnToSignIn);
   }

   /**
    * Handles the registration logic for a user with the ROLE of USER.
    * It builds the Entity, sends an username verification and checks it for
    * doing the insertion successfully.
    *
    * @param event The ActionEvent
    */
   @FXML
   private void handleSignUp(ActionEvent event) {
      String name = txtName.getText().trim();
      String email = txtEmail.getText().trim();
      String username = txtUsername.getText().trim();
      String password = txtPassword.getText().trim();

      if (!ValidatorUtil.isValidString(name, email, password)) {
         Platform.runLater(() ->
                 alert.buildAlert(Alert.AlertType.ERROR, "ERROR", "All fields are required.")
                         .show()
         );
         return;
      }
      InfoUserDto userDto = InfoUserDto.builder()
              .name(name)
              .email(email)
              .username(username)
              .password(password)
              .build();
      this.user = UserEntity.builder()
              .uuid(UUID.randomUUID())
              .name(userDto.name())
              .email(userDto.email())
              .username(userDto.username())
              .password(userDto.password())
              .role(ERole.USER)
              .token(TokenGenerator.genToken())
              .verified(false)
              .build();

      this.token = this.user.getToken();

      Runnable r = () -> EmailSender.mailTo(this.user.getEmail(), this.token);
      Thread.ofVirtual().start(r);

      checkVerification(this.token);
   }


   /**
    * Checks if the user input is equals to the token that has been sent
    * and updates the state of verification from the user
    *
    * @param token a random secure token.
    */
   private void checkVerification(String token) {
      TextInputDialog input = new TextInputDialog();
      input.setTitle("Verification code");
      input.setHeaderText("Enter the code that has sent to your username address: ");
      input.setContentText("Code: ");
      Optional<String> inputPresent = input.showAndWait();
      while (inputPresent.isPresent() && !inputPresent.get().equals(token)) {
         alert.buildAlert(Alert.AlertType.INFORMATION, "INFORMATION", "Values are not equals.")
                 .show();
         inputPresent = input.showAndWait();
      }

      inputPresent.ifPresentOrElse(_ -> {
         this.user.setVerified(true);
         this.repo.save(this.user);
         this.token = null;
         this.user = null;
         alert.buildAlert(Alert.AlertType.INFORMATION, "REGISTER", "Sign Up success, you can signIn now!").showAndWait();
         this.btnToSignIn.fire();
      }, () -> alert.buildAlert(Alert.AlertType.ERROR, "CANCEL", "Operation canceled, try again.").show());
   }

   /**
    * Handles the return to the view of login
    *
    * @param event The ActionEvent
    */
   @FXML
   private void handleReturnToSignIn(ActionEvent event) {
      try {
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/login.fxml"));
         Parent root = loader.load();
         Scene newScene = new Scene(root);

         Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
         stage.setScene(newScene);
         stage.show();
      } catch (IOException e) {
         Platform.runLater(() ->
                 alert.buildAlert(Alert.AlertType.ERROR, "ERROR", "Can't load SignIn page.")
                         .show());
         log.severe("Resource loading went wrong" + e);
      }
   }
}
