package jame.dev.controller;

import jame.dev.utils.ExecutorTabLoaderUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Controller class for build the Admin view.
 */
public class Admin {

   @FXML
   private TabPane tabPane;
   @FXML
   private Tab tabUsers;

   @FXML
   private Tab tabBooks;

   @FXML
   private Tab tabLoans;

   @FXML
   private Tab tabFines;

   /**
    * Loads the content for the Admin Tabs in a lazy way using {@link ExecutorTabLoaderUtil} class.
    */
   @FXML
   public void initialize() {
      ExecutorTabLoaderUtil.loadTab("/templates/adminPanes/Users.fxml", this.tabUsers);
      ExecutorTabLoaderUtil.loadTab("/templates/adminPanes/Books.fxml", this.tabBooks);
      ExecutorTabLoaderUtil.loadTab("/templates/adminPanes/Loans.fxml", this.tabLoans);
      ExecutorTabLoaderUtil.loadTab("/templates/adminPanes/Fines.fxml", this.tabFines);
   }
}
