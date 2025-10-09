package jame.dev.controller.admin;

import jame.dev.dtos.users.AdminDto;
import jame.dev.dtos.users.InfoUserDto;
import jame.dev.models.entitys.UserEntity;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.UserService;
import jame.dev.utils.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Log
public class Users {

   @FXML
   private TextField txtName, txtSearch, txtEmail, txtUsername;

   @FXML
   private PasswordField txtPassword;

   @FXML
   private TableView<AdminDto> tableAdmins;
   @FXML
   private TableColumn<AdminDto, UUID> colUuid;
   @FXML
   private TableColumn<AdminDto, String> colName;
   @FXML
   private TableColumn<AdminDto, String> colEmail;
   @FXML
   private TableColumn<AdminDto, String> colUsername;
   @FXML
   private TableColumn<AdminDto, String> colRole;

   @FXML
   private Button btnSave, btnClear, btnDrop;

   @FXML private Label lblName, lblEmail, lblUsername, lblPwd;

   private CRUDRepo<UserEntity> repo;

   private List<AdminDto> users;

   private UUID uuidSelected;
   private int index;

   private static final CustomAlert alert = CustomAlert.getInstance();

   /**
    * Initializes components, global data and listeners, everything of that
    * type must be in this method.
    */
   @FXML
   public void initialize() throws IOException {
      //repository
      this.repo = new UserService();
      //data
      this.users = this.repo.getAll().stream()
              .filter(u -> u.getRole() == ERole.ADMIN &&
                      u.getId().intValue() != SessionManager.getInstance().getSessionDto().id())
              .map(u -> AdminDto.builder()
                      .id(u.getId())
                      .uuid(u.getUuid())
                      .name(u.getName())
                      .email(u.getEmail())
                      .username(u.getUsername())
                      .role(u.getRole())
                      .build())
              .collect(Collectors.toList());
      //table config
      initTable();

      //buttons
      this.btnClear.setOnAction(this::handleClear);
      this.btnSave.setOnAction(this::handleSave);
      this.btnDrop.setOnAction(this::handleDelete);

      ComponentValidationUtil.addValidation(txtName, lblName, ValidatorUtil::isValidString, "Name is not valid.");
      ComponentValidationUtil.addValidation(txtEmail, lblEmail, ValidatorUtil::isEmailValid, "Email is not valid.");
      ComponentValidationUtil.addValidation(txtUsername, lblUsername, ValidatorUtil::isValidString, "Username is not valid.");
      ComponentValidationUtil.addValidation(txtPassword, lblPwd, ValidatorUtil::isValidString,
              !ValidatorUtil.pwdIsStrong(txtPassword.getText().trim()) ? "Password weak": "");
      //filter listener
      FilteredList<AdminDto> filteredData =
              new FilteredList<>(this.tableAdmins.getItems(), p -> true);
      this.txtSearch.setOnKeyTyped(key -> this.handleFilter(key, filteredData));
   }

   /**
    * This method defines the background configuration for the TableView
    * witch includes data, selection and listeners options.
    */
   @FXML
   private void initTable() {
      //columns
      this.colUuid.setCellValueFactory(data ->
              new SimpleObjectProperty<>(data.getValue().uuid()));
      this.colName.setCellValueFactory(data ->
              new SimpleStringProperty(data.getValue().name()));
      this.colEmail.setCellValueFactory(data ->
              new SimpleStringProperty(data.getValue().email()));
      this.colUsername.setCellValueFactory(data ->
              new SimpleStringProperty(data.getValue().username()));
      this.colRole.setCellValueFactory(data ->
              new SimpleStringProperty(data.getValue().role().name()));
      //selection
      this.tableAdmins.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      //set data

      ObservableList<AdminDto> observable = FXCollections.observableArrayList(users);
      tableAdmins.setItems(observable);

      //listener
      this.tableAdmins.setOnMouseClicked(_ ->
              Optional.ofNullable(this.tableAdmins.getSelectionModel().getSelectedItem())
                      .ifPresent(selected -> {
                         this.uuidSelected = selected.uuid();
                         this.index = this.tableAdmins.getSelectionModel().getSelectedIndex();
                      })
      );
   }

   /**
    * Manages the clean data for the view like:
    * text, selections and/or enable/disable buttons.
    */
   @FXML
   private void handleClear(ActionEvent e) {
      //fields
      txtName.clear();
      txtEmail.clear();
      txtUsername.clear();
      txtPassword.clear();
      txtSearch.clear();
      //selections
      this.tableAdmins.getSelectionModel().clearSelection();
      this.btnSave.setDisable(false);

      //reset globals
      this.uuidSelected = null;
      this.index = -1;
   }

   /***
    * Manages the build of data witch is going to be saved
    * by the repository, also it updates the local ArrayList and sends a communication username
    * to the new user, finally fire the clear button and shows an {@link CustomAlert}
    * for confirmation or error.
    */
   @FXML
   private void handleSave(ActionEvent e) {
      InfoUserDto userDto = InfoUserDto.builder()
              .name(txtName.getText().trim())
              .email(txtEmail.getText().trim())
              .username(txtUsername.getText().trim())
              .password(txtPassword.getText().trim())
              .build();
      UserEntity user = UserEntity.builder()
              .uuid(UUID.randomUUID())
              .name(userDto.name())
              .email(userDto.email())
              .username(userDto.username())
              .password(userDto.password())
              .role(ERole.ADMIN)
              .token(TokenGenerator.genToken())
              .verified(true)
              .build();
      //check if it's a duplicate username in the table
      boolean isDuplicateEntryIn = this.users.stream()
              .anyMatch(u -> u.email().equals(user.getEmail()) ||
                      u.username().equals(user.getUsername()));

      if (isDuplicateEntryIn) {
         alert.buildAlert(Alert.AlertType.WARNING, "WARNING", "Entry duplicated! Check Email or Username values.")
                 .show();
         this.btnClear.fire();
         return;
      }
      //add the new user if a mail has been sent to him
      Runnable emailTo = () -> {
         boolean mailSent = EmailSender.mailToWPassword(user.getEmail(), txtPassword.getText().trim());
         Platform.runLater(() -> {
            this.btnSave.setDisable(true);
            if(!mailSent){
               alert.buildAlert(Alert.AlertType.INFORMATION, "NOT FOUND", "Your email address could not be found.")
                       .show();
               return;
            }
            alert.buildAlert(Alert.AlertType.INFORMATION, "INFO", "Email sent to: " + user.getEmail() + " plase check it.")
                    .show();
            this.repo.save(user);

            AdminDto admin = AdminDto.builder()
                    .id(null)
                    .uuid(user.getUuid())
                    .name(user.getName())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .build();
            this.users.add(admin);
            this.tableAdmins.getItems().add(admin);

            //notify
            alert.buildAlert(Alert.AlertType.CONFIRMATION, "SUCCESS!", "Admin added!")
                    .show();
         });
      };
      Thread.ofVirtual().start(emailTo);
      this.btnClear.fire();
   }

   /**
    * Handles the deletions for an object of {@link UserEntity} if the user is not the
    * same as the current session.
    *
    * @param e ActionEvent
    */
   @FXML
   private void handleDelete(ActionEvent e) {
      String usernameSession = SessionManager.getInstance().getSessionDto().username();
      String usernameSelected = this.tableAdmins.getSelectionModel()
              .getSelectedItem().email();
      if (usernameSession.equals(usernameSelected)) {
         alert.buildAlert(Alert.AlertType.WARNING, "WARNING", "You can´t delete you here.")
                 .show();
         this.btnClear.fire();
         return;
      }
      alert.buildAlert(Alert.AlertType.WARNING, "DELETE",
                      "¿Do you want delete this user?")
              .showAndWait()
              .ifPresent(confirmation -> {
                 if (confirmation == ButtonType.OK) {
                    this.repo.deleteByUuid(this.uuidSelected);
                    this.tableAdmins.getItems().remove(this.index);
                    this.users.remove(this.index);
                 }
              });
      this.btnClear.fire();
   }

   /**
    * Sets a predicate in the filteredList witch is used for set the
    * data items to view in the table.
    *
    * @param key          KeyEvent
    * @param filteredList FilteredList<UserEntity>
    * @throws IllegalArgumentException if the text is not part
    *                                  of the enum {@link ERole}
    */
   @FXML
   private void handleFilter(KeyEvent key, FilteredList<AdminDto> filteredList) {
      String text = txtSearch.getText().trim();
         filteredList.setPredicate(user -> {
            if (text.isEmpty()) return true;
            try {

               return user.uuid().toString().contains(text) ||
                       user.name().contains(text) ||
                       user.email().contains(text) ||
                       user.username().contains(text) ||
                       user.role() == ERole.valueOf(text.toUpperCase());
            } catch (IllegalArgumentException e) {
               return false;
            }
         });
      this.tableAdmins.setItems(filteredList);
   }
}
