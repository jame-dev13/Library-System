package jame.dev.controller;

import jame.dev.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

/**
 * Controller for the User no admin view
 */
public class User {
   @FXML private Tab tabMyLoans;
   @FXML private Tab tabFines;
   @FXML private Tab tabBooks;
   @FXML private Tab tabStats;
   @FXML private Tab tabInfo;

   /**
    * Loads the tab panes for the view.
    */
   @FXML private void initialize(){
      try{
         Parent loans = FXMLLoader.load(Main.class.getResource("/templates/userPanes/myLoans.fxml"));
         this.tabMyLoans.setContent(loans);

         Parent fines = FXMLLoader.load(Main.class.getResource("/templates/userPanes/fines.fxml"));
         this.tabFines.setContent(fines);

         Parent books = FXMLLoader.load(Main.class.getResource("/templates/userPanes/books.fxml"));
         this.tabBooks.setContent(books);

         Parent stats = FXMLLoader.load(Main.class.getResource("/templates/userPanes/stats.fxml"));
         this.tabStats.setContent(stats);

         Parent info = FXMLLoader.load(Main.class.getResource("/templates/userPanes/info.fxml"));
         this.tabInfo.setContent(info);
      }catch(Exception e){
          throw new RuntimeException(e);
      }
   }
}
