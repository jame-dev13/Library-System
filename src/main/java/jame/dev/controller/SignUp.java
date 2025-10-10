package jame.dev.controller;

import jame.dev.dtos.users.InfoUserDto;
import jame.dev.models.entitys.UserEntity;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.UserService;
import jame.dev.utils.tools.EmailSender;
import jame.dev.utils.tools.TokenGenerator;
import jame.dev.utils.tools.ValidatorUtil;
import jame.dev.utils.ui.ComponentValidationUtil;
import jame.dev.utils.ui.CustomAlert;
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
   private Label lbName;
   @FXML
   private Label lbEmail;
   @FXML
   private Label lbUsername;
   @FXML
   private Label lbPwd;
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
   @FXML
   private final CRUDRepo<UserEntity> REPO = new UserService();
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
      btnReg.setOnAction(this::handleSignUp);
      btnToSignIn.setOnAction(this::handleReturnToSignIn);
      ComponentValidationUtil.addValidation(txtName, lbName, ValidatorUtil::isValidString, "Name not valid.");
      ComponentValidationUtil.addValidation(txtEmail, lbEmail, ValidatorUtil::isEmailValid, "Email not valid.");
      ComponentValidationUtil.addValidation(txtUsername, lbUsername, ValidatorUtil::isValidString, "Username not valid.");
      ComponentValidationUtil.addValidation(txtPassword, lbPwd, ValidatorUtil::pwdIsStrong,
              !ValidatorUtil.pwdIsStrong(txtPassword.getText()) ? "Password weak": "");
   }

   /**
    * Handles the registration logic for a user with the ROLE of USER.
    * It builds the Entity, sends a username verification and checks it for
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

      if (!ValidatorUtil.isValidString(name, username) && !ValidatorUtil.isEmailValid(email)) {
         Platform.runLater(() ->
                 alert.warningAlert("Names can't start with numbers or any other characters that is not a letter, same for email")
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

      Runnable r = () -> {
         boolean emailSent = EmailSender.mailTo(this.user.getEmail(), this.token);
         Platform.runLater(() -> {
            this.btnReg.setDisable(true);
            if (!emailSent) {
               alert.errorAlert("Can't sent the email.");
               return;
            }
            checkVerification(this.token);
         });
      };
      Thread.ofVirtual().start(r);
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
         alert.errorAlert("Values are not the same.");
         inputPresent = input.showAndWait();
      }

      inputPresent.ifPresentOrElse(_ -> {
         this.user.setVerified(true);
         this.REPO.save(this.user);
         this.token = null;
         this.user = null;
         alert.infoAlert("SignUp success, you can do login now");
         this.btnToSignIn.fire();
      }, () -> alert.warningAlert("Operation canceled, try again"));
      this.btnReg.setDisable(true);
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
                 alert.errorAlert("Can't load SignIn page."));
         log.severe("Resource loading went wrong" + e.getMessage());
      }
   }
}
