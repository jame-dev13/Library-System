package jame.dev.controller.admin;

import jame.dev.models.entitys.UserEntity;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.UserService;
import jame.dev.utils.EmailSender;
import jame.dev.utils.TokenGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.UUID;

public class Users {

    @FXML
    private ToggleButton togId, togName, togEmail, togRole;

    @FXML
    private TextField txtName, txtSearch, txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TableView<UserEntity> tableAdmins;
    @FXML
    private TableColumn<UserEntity, Integer> colUuid;
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

    @FXML
    public void initialize() {
        this.repo = new UserService();
        colUuid.setCellValueFactory(new PropertyValueFactory<>("uuid"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRole().name()));
        this.users = this.repo.getAll();
        fillTable();
        //buttons
        this.btnClear.setOnAction(this::handleClear);
        this.btnSave.setOnAction(this::handleSave);
        this.btnDrop.setOnAction(this::handleDelete);
    }

    @FXML
    private void fillTable() {
        this.tableAdmins.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ObservableList<UserEntity> users = FXCollections.observableArrayList(
                this.users.stream()
                        .filter(u -> u.getRole() == ERole.ADMIN)
                        .toList()
        );
        tableAdmins.setItems(users);
        this.tableAdmins.setOnMouseClicked(l -> {
                    this.uuidSelected = this.tableAdmins.getSelectionModel().getSelectedItem().getUuid();
                    this.index = this.tableAdmins.getSelectionModel().getFocusedIndex();
                }
        );
    }

    @FXML
    private void handleClear(ActionEvent e) {
        txtName.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtSearch.setText("");
        this.tableAdmins.getSelectionModel().clearSelection();
    }

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
            System.out.println("Email duplicated");
            return;
        }
        this.repo.save(user);
        this.users.add(user);

        Runnable r = () -> EmailSender.mailToWPassword(user.getEmail(), txtPassword.getText());
        Thread t = new Thread(r);
        t.start();

        this.tableAdmins.getItems().add(user);
        this.btnClear.fire();
    }

    @FXML
    private void handleDelete(ActionEvent e) {
        this.repo.deleteByUuid(this.uuidSelected);
        this.tableAdmins.refresh();
        uuidSelected = null;
        this.users.remove(this.index);
        this.index = -1;
    }
}
