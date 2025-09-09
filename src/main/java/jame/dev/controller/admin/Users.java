package jame.dev.controller.admin;

import jame.dev.models.entitys.UserEntity;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.UserService;
import jame.dev.utils.CustomAlert;
import jame.dev.utils.EmailSender;
import jame.dev.utils.SessionManager;
import jame.dev.utils.TokenGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Users {

   @FXML
   private ToggleButton togUuid, togName, togEmail, togRole;

   @FXML
   private TextField txtName, txtSearch, txtEmail;

   @FXML
   private PasswordField txtPassword;

   @FXML
   private TableView<UserEntity> tableAdmins;
   @FXML
   private TableColumn<UserEntity, UUID> colUuid;
   @FXML
   private TableColumn<UserEntity, String> colName;
   @FXML
   private TableColumn<UserEntity, String> colEmail;
   @FXML
   private TableColumn<UserEntity, String> colRole;

   @FXML
   private Button btnSave, btnClear, btnDrop;


   private CRUDRepo<UserEntity> repo;

   private List<UserEntity> users;

   private UUID uuidSelected;
   private int index;

   /**
    * Initializes components, global data and listeners, everything of that
    * type must be in this method.
    */
   @FXML
   public void initialize() throws IOException {
      //repository
      this.repo = new UserService();
      //data
      this.users = this.repo.getAll();
      //table config
      initTable();

      //buttons
      this.btnClear.setOnAction(this::handleClear);
      this.btnSave.setOnAction(this::handleSave);
      this.btnDrop.setOnAction(this::handleDelete);
      //filter listener
      FilteredList<UserEntity> filteredData =
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
      this.colUuid.setCellValueFactory(new PropertyValueFactory<>("uuid"));
      this.colName.setCellValueFactory(new PropertyValueFactory<>("name"));
      this.colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
      this.colRole.setCellValueFactory(data ->
              new SimpleStringProperty(data.getValue().getRole().name()));
      //selection
      this.tableAdmins.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      //set data
      ObservableList<UserEntity> users = FXCollections.observableArrayList(
              this.users.stream()
                      .filter(u -> u.getRole() == ERole.ADMIN)
                      .collect(Collectors.toList())
      );
      tableAdmins.setItems(users);

      //listener
      this.tableAdmins.setOnMouseClicked(_ ->
              Optional.ofNullable(this.tableAdmins.getSelectionModel().getSelectedItem())
                      .ifPresent(selected -> {
                         this.uuidSelected = selected.getUuid();
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
      txtName.setText("");
      txtEmail.setText("");
      txtPassword.setText("");
      txtSearch.setText("");
      //selections
      this.tableAdmins.getSelectionModel().clearSelection();
      //toggle buttons
      this.togUuid.setSelected(false);
      this.togName.setSelected(false);
      this.togEmail.setSelected(false);
      this.togRole.setSelected(false);

      //reset globals
      this.uuidSelected = null;
      this.index = -1;
   }

   /***
    * Manages the build of data witch is going to be saved
    * by the repository, also it updates the local ArrayList and sends a communication email
    * to the new user, finally fire the clear button and shows an {@link CustomAlert}
    * for confirmation or error.
    */
   @FXML
   private void handleSave(ActionEvent e) {
      UserEntity user = UserEntity.builder()
              .uuid(UUID.randomUUID())
              .name(txtName.getText().trim())
              .email(txtEmail.getText().trim())
              .password(BCrypt.hashpw(txtPassword.getText().trim(), BCrypt.gensalt(12)))
              .role(ERole.ADMIN)
              .token(TokenGenerator.genToken())
              .verified(true)
              .build();
      //check if it's a duplicate email in the table
      boolean isEmailIn = this.users.stream()
              .anyMatch(u -> u.getEmail().equals(user.getEmail()));

      if (isEmailIn) {
         CustomAlert.getInstance()
                 .buildAlert(Alert.AlertType.WARNING, "WARNING", "Email duplicated!")
                 .show();
         this.btnClear.fire();
         return;
      }
      //add the new user
      this.repo.save(user);
      this.users.add(user);
      this.tableAdmins.getItems().add(user);

      //send email with his credentials
      Runnable r = () -> EmailSender.mailToWPassword(user.getEmail(), txtPassword.getText());
      Thread.ofVirtual().start(r);

      //notify
      CustomAlert.getInstance()
              .buildAlert(Alert.AlertType.CONFIRMATION, "SUCCESS!", "Admin added!")
              .show();
      this.btnClear.fire();
   }

   /**
    * Handles the deletions for an object of {@link UserEntity} if the user is not the
    * same as the current session.
    * @param e ActionEvent
    */
   @FXML
   private void handleDelete(ActionEvent e) {
      String usernameSession = SessionManager.getInstance().getSessionDto().email();
      String usernameSelected = this.tableAdmins.getSelectionModel().getSelectedItem().getEmail();
      if (usernameSession.equals(usernameSelected)) {
         CustomAlert.getInstance()
                 .buildAlert(Alert.AlertType.WARNING, "WARNING", "You can´t delete you here.")
                 .show();
         return;
      }
      CustomAlert.getInstance()
              .buildAlert(Alert.AlertType.WARNING, "DELETE",
                      "¿Do you want delete this fine?")
              .showAndWait()
              .ifPresent(confirmation -> {
                 if (confirmation == ButtonType.OK) {
                    this.repo.deleteByUuid(this.uuidSelected);
                    this.tableAdmins.refresh();
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
   private void handleFilter(KeyEvent key, FilteredList<UserEntity> filteredList) {
      String text = txtSearch.getText().trim();
      try {
         filteredList.setPredicate(user -> {
            if (text.isEmpty()) return true;
            return user.getUuid().toString().contains(text) ||
                    user.getName().contains(text) ||
                    user.getEmail().contains(text) ||
                    user.getRole() == ERole.valueOf(text.toUpperCase());
         });
         this.tableAdmins.setItems(filteredList);
      } catch (IllegalArgumentException e) {
         throw new RuntimeException(e);
      }
   }
}
