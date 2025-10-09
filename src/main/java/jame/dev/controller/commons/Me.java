package jame.dev.controller.commons;

import jame.dev.dtos.users.InfoUserDto;
import jame.dev.dtos.users.SessionDto;
import jame.dev.models.entitys.UserEntity;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.UserService;
import jame.dev.utils.ui.ComponentValidationUtil;
import jame.dev.utils.ui.CustomAlert;
import jame.dev.utils.session.SessionManager;
import jame.dev.utils.tools.ValidatorUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

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
   private CRUDRepo<UserEntity> repo;

   @FXML
   private void initialize() throws IOException {
      this.repo = new UserService();
      this.setInfo();
      this.btnClear.setOnAction(this::handleClear);
      this.btnUpdate.setOnAction(this::handleUpdate);
      ComponentValidationUtil.addValidation(txtName, lblName, ValidatorUtil::isValidString, "Name not valid.");
      ComponentValidationUtil.addValidation(txtEmail, lblEmail, ValidatorUtil::isEmailValid, "Email not valid.");
      ComponentValidationUtil.addValidation(txtUsername, lblUsername, ValidatorUtil::isValidString, "Username not valid.");
      ComponentValidationUtil.addValidation(txtPassword, lblPwd, ValidatorUtil::pwdIsStrong,
              !ValidatorUtil.pwdIsStrong(txtPassword.getText()) ? "Password weak": "");
   }

   @FXML
   private void handleClear(ActionEvent event) {
      this.setInfo();
   }

   @FXML
   private void handleUpdate(ActionEvent event) {
      InfoUserDto userDto = InfoUserDto.builder()
              .name(txtName.getText().trim())
              .email(txtEmail.getText().trim())
              .username(txtUsername.getText().trim())
              .password(txtPassword.getText().trim())
              .build();
      alert.buildAlert(Alert.AlertType.CONFIRMATION, "CONFIRMATION", "Do you want update your own info?")
              .showAndWait()
              .ifPresent(confirmation -> {
                 if (confirmation == ButtonType.OK) {
                    this.repo.findByUuid(session.getSessionDto().uuid())
                            .ifPresentOrElse(userEntity -> {
                               userEntity.setName(userDto.name());
                               userEntity.setEmail(userDto.email());
                               userEntity.setUsername(userDto.username());
                               userEntity.setPassword(userDto.password());
                               this.repo.update(userEntity);
                               session.logout();
                               session.login(SessionDto.builder()
                                       .id(userEntity.getId())
                                       .uuid(userEntity.getUuid())
                                       .username(userEntity.getUsername())
                                       .role(userEntity.getRole()).build());
                               alert.buildAlert(Alert.AlertType.INFORMATION, "INFO", "Information updated successfully.").show();
                            }, () -> alert.buildAlert(Alert.AlertType.ERROR, "ERROR", "Information required not found.").show());
                 }
              });
      this.btnClear.fire();
   }

   @FXML
   private void setInfo() {
      this.repo.findByUuid(session.getSessionDto().uuid())
              .ifPresentOrElse(entity -> {
                 txtName.setText(entity.getName());
                 txtEmail.setText(entity.getEmail());
                 txtUsername.setText(entity.getUsername());
              }, () -> alert.buildAlert(Alert.AlertType.ERROR, "ERROR", "No information available!").show());
   }
}
