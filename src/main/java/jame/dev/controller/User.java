package jame.dev.controller;

import jame.dev.dtos.SessionDto;
import jame.dev.utils.CustomAlert;
import jame.dev.utils.ExecutorTabLoaderUtil;
import jame.dev.utils.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import lombok.extern.java.Log;

import java.io.IOException;

/**
 * Controller for build the User Client view.
 */
@Log
public class User {
   @FXML
   private Button btnLogout;
   @FXML
   private Tab tabFines;
   @FXML
   private Tab tabBooks;
   @FXML
   private Tab tabInfo;

   /**
    * Loads the content for the user Tabs in a lazy way using {@link ExecutorTabLoaderUtil} class.
    */
   @FXML
   private void initialize() {
      this.btnLogout.setOnAction(this::handleLogout);
      ExecutorTabLoaderUtil.loadTab("/templates/commons/Me.fxml", this.tabInfo);
      ExecutorTabLoaderUtil.loadTab("/templates/userPanes/books.fxml", this.tabBooks);
      ExecutorTabLoaderUtil.loadTab("/templates/userPanes/fines.fxml", this.tabFines);
   }
   @FXML
   private void handleLogout(ActionEvent event){
      SessionManager manager = SessionManager.getInstance();
      SessionDto dto = manager.getSessionDto();
      if(dto != null){
         manager.logout();
         try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
         }catch(IOException e){
            Platform.runLater(() ->
                    CustomAlert.getInstance()
                            .buildAlert(Alert.AlertType.ERROR, "ERROR", "Error loading the view.")
                            .show());
            log.severe("Error loading the resource: " + e);
         }
      }
      else{
         Platform.runLater(() ->
            CustomAlert.getInstance()
                    .buildAlert(Alert.AlertType.ERROR, "ERROR", "No Session active")
                    .show());
      }
   }
}
