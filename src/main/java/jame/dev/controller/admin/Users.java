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
import javafx.scene.layout.VBox;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @FXML
    private VBox boxSearch;

    private CRUDRepo<UserEntity> repo;

    private List<UserEntity> users;

    private UUID uuidSelected;
    private int index;

    @FXML
    public void initialize() {
        this.repo = new UserService();
        colUuid.setCellValueFactory(new PropertyValueFactory<>("uuid"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRole().name()));
        this.users = this.repo.getAll();
        initTable();

        //buttons
        this.btnClear.setOnAction(this::handleClear);
        this.btnSave.setOnAction(this::handleSave);
        this.btnDrop.setOnAction(this::handleDelete);
        //Field

        FilteredList<UserEntity> filteredData =
                new FilteredList<>(this.tableAdmins.getItems(), p -> true);
        this.txtSearch.setOnKeyTyped(_ -> {
            String text = txtSearch.getText().trim();

            filteredData.setPredicate(u -> {
                if (text.isEmpty()) return true; //show everything

                if (this.togUuid.isSelected()) {
                    return u.getUuid().toString().contains(text);
                } else if (this.togName.isSelected()) {
                    return u.getName().toLowerCase().contains(text.toLowerCase());
                } else if (this.togEmail.isSelected()) {
                    return u.getEmail().toLowerCase().contains(text.toLowerCase());
                } else {
                    try {
                        return u.getRole() == ERole.valueOf(text.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                }
            });
            this.tableAdmins.setItems(filteredData);
        });
    }

    /**
     * This method defines the background configuration for the TableView
     * witch includes data, selection and listeners options.
     */
    @FXML
    private void initTable() {
        this.tableAdmins.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ObservableList<UserEntity> users = FXCollections.observableArrayList(
                this.users.stream()
                        .filter(u -> u.getRole() == ERole.ADMIN)
                        .toList()
        );
        tableAdmins.setItems(users);
        this.tableAdmins.setOnMouseClicked(l -> {
                    Optional.ofNullable(this.tableAdmins.getSelectionModel().getSelectedItem())
                            .ifPresent(selected -> {
                                this.uuidSelected = selected.getUuid();
                                this.index = this.tableAdmins.getSelectionModel().getSelectedIndex();
                            });
                }
        );
    }

    /**
     * This method is the manager for clean things in the view like:
     * text, selections and/or enable/disable buttons.
     */
    @FXML
    private void handleClear(ActionEvent e) {
        txtName.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtSearch.setText("");
        this.tableAdmins.getSelectionModel().clearSelection();
        this.togUuid.setSelected(false);
        this.togName.setSelected(false);
        this.togEmail.setSelected(false);
        this.togRole.setSelected(false);
    }

    /***
     * This method is the manager to build the data witch is going to be saved
     * by the repository, also it updates the local ArrayList and sends a communication email
     * to the new user, finally fire the clear button and show an Alert.
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

        boolean isEmailIn = this.users.stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()));

        if (isEmailIn) {
            CustomAlert.getInstance()
                    .buildAlert(Alert.AlertType.WARNING, "WARNING", "Email duplicated!")
                    .showAndWait();
            return;
        }
        this.repo.save(user);
        this.users.add(user);

        Runnable r = () -> EmailSender.mailToWPassword(user.getEmail(), txtPassword.getText());
        Thread t = new Thread(r);
        t.start();

        this.tableAdmins.getItems().add(user);
        this.btnClear.fire();
        CustomAlert.getInstance()
                .buildAlert(Alert.AlertType.CONFIRMATION, "SUCCESS!", "Admin added!")
                .showAndWait();
    }


    /**
     * this method manages the delete logic for a user in the view
     * based on the selected uuid and index and checks that the selected
     * row info doesn't match with the session data.
     */
    @FXML
    private void handleDelete(ActionEvent e) {
        String usernameSession = SessionManager.getInstance().getUsername();
        String usernameSelected = this.tableAdmins.getSelectionModel().getSelectedItem().getEmail();
        if (usernameSession.equals(usernameSelected)) {
            CustomAlert.getInstance()
                    .buildAlert(Alert.AlertType.WARNING, "WARNING", "You can´t delete you here.")
                    .showAndWait();
            return;
        }
        this.repo.deleteByUuid(this.uuidSelected);
        this.tableAdmins.refresh();
        uuidSelected = null;
        this.users.remove(this.index);
        this.index = -1;
    }
}
