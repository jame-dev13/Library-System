package jame.dev.controller;

import jame.dev.Main;
import jame.dev.models.entitys.UserEntity;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.UserService;
import jame.dev.utils.EmailSender;
import jame.dev.utils.TokenGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SignUp {

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnReg;

    @FXML
    private Button btnToSignIn;

    private CRUDRepo<UserEntity> repo;
    private UserEntity user;
    private String token;

    @FXML
    private void initialize() throws IOException{
        this.repo = new UserService();
        btnReg.setOnAction(this::handleSignUp);
        btnToSignIn.setOnAction(this::handleReturnToSignIn);
    }

    @FXML
    private void handleSignUp(ActionEvent event){
        String name = txtName.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        this.user = UserEntity.builder()
                .uuid(UUID.randomUUID())
                .name(name)
                .email(email)
                .password(BCrypt.hashpw(password, BCrypt.gensalt(12)))
                .role(ERole.USER)
                .token(TokenGenerator.genToken())
                .verified(false)
                .build();
        this.token = this.user.getToken();
        Runnable r = () -> EmailSender.mailTo(this.user.getEmail(), this.token);
        new Thread(r).start();
        checkVerification(this.token);
    }

    private void checkVerification(String token){
        boolean isValid = false;
        TextInputDialog input = new TextInputDialog();
        input.setTitle("Verification code");
        input.setHeaderText("Enter the code that has sent to your email address: ");
        input.setContentText("Code: ");

        while(!isValid){
            Optional<String> inputPresent = input.showAndWait();
            if(inputPresent.isPresent()){
                String value = inputPresent.get();
                if(value.equals(token)){
                    showAlert(Alert.AlertType.INFORMATION, "Verification Success :)")
                            .show();
                    this.user.setVerified(true);
                    this.repo.save(this.user);
                    isValid = true;
                    this.token = null;
                    this.user = null;
                    this.btnToSignIn.fire();
                }else showAlert(Alert.AlertType.ERROR, "Token invalid!")
                        .showAndWait();
            }
        }
    }

    @FXML
    private void handleReturnToSignIn(ActionEvent event){
        try{
            Parent root = FXMLLoader
                    .load(Objects.requireNonNull(
                            Main.class.getResource("/templates/login.fxml")
                    ));
            Scene newScene = new Scene(root);

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(newScene);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private Alert showAlert(Alert.AlertType type, String context){
        Alert alert = new Alert(type);
        alert.setTitle("ALERT!");
        alert.setHeaderText("Verification!");
        alert.setContentText(context);
        return alert;
    }
}
