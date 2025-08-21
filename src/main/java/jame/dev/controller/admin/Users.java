package jame.dev.controller.admin;

import jame.dev.models.entitys.UserEntity;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;

import java.awt.*;

public class Users {

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TableView<UserEntity> tableAdmins;

    @FXML
    private Button btnSave;
    @FXML
    Button btnClear;
    @FXML
    Button btnUpdate;
    @FXML
    Button btnDrop;

    private CRUDRepo<UserEntity> repo;
    @FXML
    public void initialize(){
        this.repo = new UserService();
    }
}
