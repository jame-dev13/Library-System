package jame.dev.controller.commons;

import jame.dev.dtos.users.InfoUserDto;
import jame.dev.dtos.users.SessionDto;
import jame.dev.models.entitys.UserEntity;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.UserService;
import jame.dev.utils.session.SessionManager;
import jame.dev.utils.tools.ValidatorUtil;
import jame.dev.utils.ui.ComponentValidationUtil;
import jame.dev.utils.ui.CustomAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.extern.java.Log;

import java.io.IOException;

/**
 * Controller class that gives functionality to the view from Me.fxml
 */
@Log
public class Me {

   @FXML
   private Button btnUpdate;
   @FXML
   private Button btnClear;
   @FXML
   private TextField txtName;
   @FXML
   private TextField txtEmail;
   @FXML
   private TextField txtUsername;
   @FXML
   private TextField txtPassword;
   @FXML
   private Label lblName, lblEmail, lblUsername, lblPwd;

   private static final SessionManager session = SessionManager.getInstance();
   private static final CustomAlert alert = CustomAlert.getInstance();
   private static final CRUDRepo<UserEntity> REPO = new UserService();

   /**
    * Init all components and his behavior in the view.
    * @throws IOException if it can't load the components
    */
   @FXML
   private void initialize() throws IOException {
      this.setInfo();
      this.btnClear.setOnAction(this::handleClear);
      this.btnUpdate.setOnAction(this::handleUpdate);
      ComponentValidationUtil.addValidation(txtName, lblName, ValidatorUtil::isValidName, "Name not valid.");
      ComponentValidationUtil.addValidation(txtEmail, lblEmail, ValidatorUtil::isEmailValid, "Email not valid.");
      ComponentValidationUtil.addValidation(txtUsername, lblUsername, ValidatorUtil::isValidName, "Username not valid.");
      ComponentValidationUtil.addValidation(txtPassword, lblPwd, ValidatorUtil::pwdIsStrong,
              !ValidatorUtil.pwdIsStrong(txtPassword.getText()) ? "Password weak" : "");
   }

   /**
    * re-set the info on a click.
    * @param event
    */
   @FXML
   private void handleClear(ActionEvent event) {
      this.setInfo();
   }

   /**
    * Updates the current logged user info, if all fields are filled.
    * @param event
    */
   @FXML
   private void handleUpdate(ActionEvent event) {
      if(txtPassword.getText().isBlank()){
         alert.errorAlert("Password field empty");
         return;
      }
      alert.updateConfirmAlert("Do you want update your own info?")
              .ifPresent(confirmation -> {
                 if (confirmation == ButtonType.OK) {
                    REPO.findByUuid(session.getSessionDto().uuid())
                            .ifPresentOrElse(userEntity -> {
                               this.update(userEntity);
                               alert.infoAlert("Information updated successfully.");
                            }, () -> alert.errorAlert("Information required not found."));
                 }
              });
      this.btnClear.fire();
   }

   /**
    * sets the fields with the user info, so he can see what he could change.
    */
   @FXML
   private void setInfo() {
      REPO.findByUuid(session.getSessionDto().uuid())
              .ifPresentOrElse(entity -> {
                 txtName.setText(entity.getName());
                 txtEmail.setText(entity.getEmail());
                 txtUsername.setText(entity.getUsername());
              }, () -> alert.warningAlert("No information available!"));
   }

   /**
    * Build an input {@link InfoUserDto} object to set the UserEntity with his data,
    * then update the record associated in the db and session does logout and login again.
    * @param userEntity the {@link UserEntity} object.
    */
   private void update(UserEntity userEntity) {
      InfoUserDto userDto = InfoUserDto.builder()
              .name(txtName.getText().trim())
              .email(txtEmail.getText().trim())
              .username(txtUsername.getText().trim())
              .password(txtPassword.getText().trim())
              .build();
      userEntity.setName(userDto.name());
      userEntity.setEmail(userDto.email());
      userEntity.setUsername(userDto.username());
      userEntity.setPassword(userDto.password());
      REPO.update(userEntity);
      session.logout();
      session.login(SessionDto.builder()
              .id(userEntity.getId())
              .uuid(userEntity.getUuid())
              .username(userEntity.getUsername())
              .role(userEntity.getRole()).build());
   }
}