package jame.dev.controller;

import jame.dev.Main;
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
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * Controller class for build the Admin view.
 */
@Log
public class Admin {

   @FXML private TabPane tabPane;
   @FXML
   private Button btnLogout;
   @FXML
   private Tab tabUsers;

   @FXML
   private Tab tabBooks;

   @FXML
   private Tab tabLoans;

   @FXML
   private Tab tabFines;
   @FXML
   private Tab tabMe;

   private static final URL URL_FINES = Main.class.getResource("/templates/adminPanes/Fines.fxml");

   /**
    * Loads the content for the Admin Tabs in a lazy way using {@link ExecutorTabLoaderUtil} class.
    */
   @FXML
   public void initialize() {
      this.btnLogout.setOnAction(this::handleLogout);
      tabPane.getSelectionModel()
              .selectedItemProperty()
              .addListener((observableValue, oldTab, newTab) -> {
                 if(newTab.equals(tabFines)) {
                    try {
                       Optional.ofNullable(URL_FINES).orElseThrow();
                       Parent root = FXMLLoader.load(URL_FINES);
                       tabFines.setContent(root);
                    } catch (IOException e) {
                       throw new RuntimeException(e);
                    }
                 }
              });
      ExecutorTabLoaderUtil.loadTab("/templates/adminPanes/Users.fxml", this.tabUsers);
      ExecutorTabLoaderUtil.loadTab("/templates/adminPanes/Books.fxml", this.tabBooks);
      ExecutorTabLoaderUtil.loadTab("/templates/adminPanes/Loans.fxml", this.tabLoans);
      ExecutorTabLoaderUtil.loadTab("/templates/adminPanes/Fines.fxml", this.tabFines);
      ExecutorTabLoaderUtil.loadTab("/templates/commons/Me.fxml", this.tabMe);

   }

   @FXML
   private void handleLogout(ActionEvent event) {
      SessionManager manager = SessionManager.getInstance();
      SessionDto dto = manager.getSessionDto();
      if (dto != null) {
         manager.logout();
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
         } catch (IOException e) {
            Platform.runLater(() ->
                    CustomAlert.getInstance()
                            .buildAlert(Alert.AlertType.ERROR, "ERROR", "Error loading the view.")
                            .show());
            log.severe("Error loading the resource: " + e);
         }
      } else {
         Platform.runLater(() ->
                 CustomAlert.getInstance()
                         .buildAlert(Alert.AlertType.ERROR, "ERROR", "No Session active")
                         .show());
      }
   }
}
