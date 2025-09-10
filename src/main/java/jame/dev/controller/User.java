package jame.dev.controller;

import jame.dev.utils.ExecutorTabLoaderUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Controller for build the User Client view.
 */
public class User {
   @FXML
   private TabPane tabPane;
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
      ExecutorTabLoaderUtil.loadTab("/templates/userPanes/info.fxml", this.tabInfo);
      ExecutorTabLoaderUtil.loadTab("/templates/userPanes/books.fxml", this.tabBooks);
      ExecutorTabLoaderUtil.loadTab("/templates/userPanes/fines.fxml", this.tabFines);
   }
}
