package jame.dev.utils;

import jame.dev.repositorys.CRUDRepo;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import lombok.extern.java.Log;

import java.util.List;

@Log
public class RefreshTable {
   private static volatile RefreshTable instance;
   private RefreshTable(){}

   public static synchronized RefreshTable getInstance(){
      if(instance == null) instance = new RefreshTable();
      return instance;
   }

   public <T> List<T> refresh(TableView<T> table, CRUDRepo<T> repo){
      List<T> refreshedItems = repo.getAll();
      table.setItems(FXCollections.observableArrayList(refreshedItems));
      return refreshedItems;
   }
}
