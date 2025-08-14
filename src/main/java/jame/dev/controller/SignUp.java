package jame.dev.controller;

import jame.dev.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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

    @FXML
    private void initialize(){
        btnReg.setOnAction(this::handleSignUp);
        btnToSignIn.setOnAction(this::handleReturnToSignIn);
    }

    @FXML
    private void handleSignUp(ActionEvent event){
        String name = txtName.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
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
