package jame.dev.controller;

import jame.dev.dtos.users.SessionDto;
import jame.dev.utils.loader.ExecutorTabLoaderUtil;
import jame.dev.utils.session.GlobalNotificationChange;
import jame.dev.utils.session.SessionManager;
import jame.dev.utils.ui.CustomAlert;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.Map;

/**
 * Controller class for build the Admin view.
 */
@Log
public class Admin {

   @FXML
   private TabPane tabPane;
   @FXML
   private Button btnLogout;
   @FXML
   private Tab tabUsers;

   @FXML
   private Tab tabBooks;

   @FXML
   private Tab tabLoans;
   @FXML
   private Tab tabMe;

   private static final CustomAlert ALERT = CustomAlert.getInstance();
   private static final SessionManager MANAGER = SessionManager.getInstance();
   private static final Map<String, Integer> changes = GlobalNotificationChange.getInstance().getChanges();

   /**
    * initializes the actions for the components in the view.
    * Load all the tabs in parallel and sets his content.
    * See in {@link ExecutorTabLoaderUtil} class.
    */
   @FXML
   public void initialize() {
      this.btnLogout.setOnAction(this::handleLogout);
      ExecutorTabLoaderUtil.loadTab("/templates/adminPanes/Users.fxml", this.tabUsers);
      ExecutorTabLoaderUtil.loadTab("/templates/adminPanes/Books.fxml", this.tabBooks);
      ExecutorTabLoaderUtil.loadTab("/templates/adminPanes/Loans.fxml", this.tabLoans);
      ExecutorTabLoaderUtil.loadTab("/templates/commons/Me.fxml", this.tabMe);
   }

   /**
    * Does the Listener for the logout click action.
    * It gets the singleton instance of {@link SessionManager} manager, to get access to the {@link SessionDto} object that is already logged
    * and if present calls {@code manager.logout();} and then loads the route
    * to the login page, setting a new scene and get the current stage.
    * @param event the ActionEvent
    */
   @FXML
   private void handleLogout(ActionEvent event) {
      SessionDto dto = MANAGER.getSessionDto();
      if (dto != null) {
         MANAGER.logout();
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
         } catch (IOException e) {
            Platform.runLater(() ->
                    ALERT.errorAlert("Error loading the view"));
            log.severe("Error loading the resource: " + e);
         }
      } else {
         Platform.runLater(() ->
                 ALERT.errorAlert("No session present."));
      }
   }
}
