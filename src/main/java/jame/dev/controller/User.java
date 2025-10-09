package jame.dev.controller;

import jame.dev.dtos.users.SessionDto;
import jame.dev.utils.loader.ExecutorTabLoaderUtil;
import jame.dev.utils.session.EGlobalNames;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for build the User Client view.
 */
@Log
public class User {
   @FXML
   private TabPane tabPane;
   @FXML
   private Tab tabMyLoans;
   @FXML
   private Tab tabResume;
   @FXML
   private Tab tabFines;
   @FXML
   private Tab tabBooks;
   @FXML
   private Tab tabMe;
   @FXML
   private Button btnLogout;

   private static final CustomAlert ALERT = CustomAlert.getInstance();
   private static final Map<String, Integer> changes = GlobalNotificationChange.getInstance().getChanges();

   /**
    * Loads the content for the user Tabs in a lazy way using {@link ExecutorTabLoaderUtil} class.
    */
   @FXML
   private void initialize() {
      this.btnLogout.setOnAction(this::handleLogout);
      Thread.ofVirtual().start(() -> this.tabPane.getSelectionModel().selectedItemProperty()
              .addListener((_, _, newTab) -> {
                 if (newTab.equals(this.tabMyLoans) && changes.containsKey(EGlobalNames.LOAN_CLIENT.name())) {
                    changes.remove(EGlobalNames.LOAN_CLIENT.name());
                    ExecutorTabLoaderUtil
                            .reLoad("/templates/userPanes/myLoans.fxml", this.tabMyLoans);
                 }
                 if (newTab.equals(this.tabResume) && (changes.containsKey(EGlobalNames.HISTORY.name()))) {
                    changes.remove(EGlobalNames.HISTORY.name());
                    ExecutorTabLoaderUtil
                            .reLoad("/templates/userPanes/resume.fxml", this.tabResume);
                 }
              }));
      ExecutorTabLoaderUtil.loadTab("/templates/commons/Me.fxml", this.tabMe);
      ExecutorTabLoaderUtil.loadTab("/templates/userPanes/books.fxml", this.tabBooks);
      ExecutorTabLoaderUtil.loadTab("/templates/userPanes/fines.fxml", this.tabFines);
      ExecutorTabLoaderUtil.loadTab("/templates/userPanes/resume.fxml", this.tabResume);
      ExecutorTabLoaderUtil.loadTab("/templates/userPanes/myLoans.fxml", this.tabMyLoans);
   }

   @FXML
   private void handleLogout(ActionEvent event) {
      SessionManager manager = SessionManager.getInstance();
      SessionDto dto = manager.getSessionDto();
      Optional.ofNullable(dto)
              .ifPresentOrElse(_ -> {
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
                                    ALERT.buildAlert(Alert.AlertType.ERROR, "ERROR", "Error loading the view.")
                                            .show());
                            log.severe("Error loading the resource: " + e);
                         }
                      },
                      () -> ALERT.buildAlert(Alert.AlertType.ERROR, "ERROR", "No Session active")
                              .show());
   }
}
