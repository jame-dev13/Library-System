package jame.dev.controller.user;

import jame.dev.dtos.fines.FineUserDto;
import jame.dev.service.FineService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.time.LocalDate;

public class Fines {
   @FXML private TableView<FineUserDto> tableFines;
   @FXML private TableColumn<FineUserDto, String> colCause;
   @FXML private TableColumn<FineUserDto, LocalDate> colExp;
   @FXML private TableColumn<FineUserDto, Integer> colDays;

   @FXML private void initialize() throws IOException{
      initTable();
   }

   @FXML private void initTable(){
      //cols
      colCause.setCellValueFactory(data ->
              new SimpleStringProperty(data.getValue().cause()));
      colExp.setCellValueFactory(data ->
              new SimpleObjectProperty<>(data.getValue().expiration()));
      colDays.setCellValueFactory(data ->
              new SimpleIntegerProperty(data.getValue().daysRemaining()).asObject());
      //data
      ObservableList<FineUserDto> observableList = FXCollections.observableArrayList(
              new FineService().getJoinsAll()
                      .stream()
                      .filter(l -> l.daysRemaining() > 0)
                      .toList()
      );
      this.tableFines.setItems(observableList);
      this.tableFines.getSelectionModel().setCellSelectionEnabled(false);
   }
}
