package jame.dev.controller;

import jame.dev.Main;
import jame.dev.models.entitys.UserEntity;
import jame.dev.models.enums.ERole;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.UserService;
import jame.dev.utils.EmailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
        Supplier<String> genToken = () -> {
            String token;
            byte[] bytes = new byte[8];
            new SecureRandom().nextBytes(bytes);
            String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            token = base64.replaceAll("[^A-Za-z0-9]", "").toUpperCase().substring(0,6);
            return token;
        };
        String name = txtName.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        this.user = UserEntity.builder()
                .name(name)
                .email(email)
                .password(BCrypt.hashpw(password, BCrypt.gensalt(12)))
                .role(ERole.USER)
                .token(genToken.get())
                .verified(false)
                .build();
        this.token = this.user.getToken();
        EmailSender.mailTo(this.user.getEmail(), this.token);
        checkVerification(this.token);
    }

    private void checkVerification(String token){
        Consumer<String> verifyCode = tk ->{
            if(token.equals(tk)){
                System.out.println("Verification success. ");
                this.user.setVerified(true);
                this.repo.save(this.user);
                this.btnToSignIn.fire();
            }else System.out.println("Verification went wrong. ");
        };
        TextInputDialog input = new TextInputDialog();
        input.setTitle("Verification code");
        input.setHeaderText("Enter the code that has sent to your email address: ");
        input.setContentText("Code: ");
        Optional<String> isInputPresent = input.showAndWait();
        isInputPresent.ifPresentOrElse(verifyCode, ()-> {
            throw new NullPointerException("Value is Null. ");
        });
    }

    @FXML
    private void handleReturnToSignIn(ActionEvent event){
        try{
            Parent root = FXMLLoader.load(Main.class.getResource("/templates/login.fxml"));
            Scene newScene = new Scene(root);

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(newScene);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
