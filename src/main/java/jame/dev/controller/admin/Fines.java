package jame.dev.controller.admin;

import jame.dev.dtos.fines.FineDetailsDto;
import jame.dev.models.entitys.FineEntity;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.repositorys.Joinable;
import jame.dev.service.FineService;
import jame.dev.service.joins.FineDetailsService;
import jame.dev.utils.ui.CustomAlert;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller class to gives functionality to the view associated  with the Fines.
 * @author jame-dev13
 */
@Log
public class Fines{

   @FXML
   private TextArea txtNewCause;
   @FXML
   private Button btnDelete;
   @FXML
   private Button btnUpdate;
   @FXML
   private Button btnClear;
   @FXML
   private TableView<FineDetailsDto> tableFines;
   @FXML
   private TableColumn<FineDetailsDto, Integer> colIdUser;
   @FXML
   private TableColumn<FineDetailsDto, String> colName;
   @FXML
   private TableColumn<FineDetailsDto, String> colCause;
   @FXML
   private TableColumn<FineDetailsDto, Integer> colDays;
   @FXML
   private TextField txtFilter;

   private static final Joinable<FineDetailsDto> FINES_DETAILS = new FineDetailsService();
   private static final CRUDRepo<FineEntity> REPO = new FineService();
   private static List<FineDetailsDto> fines;
   private int indexSelected;
   private UUID uuidSelected;
   private static final CustomAlert ALERT = CustomAlert.getInstance();
   private static FilteredList<FineDetailsDto> filteredList;

   /**
    * Init listeners for the components.
    * @throws IOException
    */
   @FXML
   private void initialize() throws IOException {
      this.btnClear.setOnAction(this::handleClear);
      this.btnUpdate.setOnAction(this::handleUpdateCause);
      this.btnDelete.setOnAction(this::handleDeleteFine);
      this.txtFilter.setOnKeyTyped(k -> this.handleFilter(k, filteredList));
   }

   /**
    * Configures the columns and the table data witch they are going to store.
    * Selection, listeners and filter are configure here too.
    */
   @FXML
   private void tableConfig() {
      //cols
      colName.setCellValueFactory(data ->
              new SimpleStringProperty(data.getValue().nameUser()));
      colIdUser.setCellValueFactory(data ->
              new SimpleIntegerProperty(data.getValue().idUser()).asObject());
      colCause.setCellValueFactory(data ->
              new SimpleStringProperty(data.getValue().cause()));
      colDays.setCellValueFactory(data ->
              new SimpleIntegerProperty(data.getValue().daysRemaining()).asObject());
      //selection
      this.tableFines.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

      //data
      ObservableList<FineDetailsDto> observable = FXCollections.observableArrayList(fines);
      this.tableFines.setItems(observable);

      //listeners
      this.tableFines.setOnMouseClicked(m -> {
         Optional.ofNullable(this.tableFines.getSelectionModel().getSelectedItem())
                 .ifPresent(this::onSelection);
      });
      filteredList = new FilteredList<>(this.tableFines.getItems(), _ -> true);
   }

   /**
    * Cleans up the fields, aux variables and disable or enables buttons.
    * @param event the Action Event.
    */
   @FXML
   private void handleClear(ActionEvent event) {
      this.tableFines.getSelectionModel().clearSelection();
      btnDelete.setDisable(true);
      btnUpdate.setDisable(true);
      txtNewCause.clear();
      txtFilter.clear();
      this.uuidSelected = null;
      this.indexSelected = -1;
   }

   /**
    * Handles the listener to update the property 'cause' for a {@link FineEntity} object
    * @param event the Action Event.
    */
   @FXML
   private void handleUpdateCause(ActionEvent event) {
      ALERT.updateConfirmAlert("Do toy want to update this fine cause?")
              .ifPresent(confirmation -> {
                 if (confirmation == ButtonType.OK) {
                    REPO.findByUuid(uuidSelected)
                            .ifPresentOrElse(this::updateCause,
                                    () -> ALERT.warningAlert("Can't find the associated fine."));
                 }
              });
      this.btnClear.fire();
   }

   /**
    * Listener method that handles the deletion action for an {@link FineEntity} object.
    * @param event the Action Event.
    */
   @FXML
   private void handleDeleteFine(ActionEvent event) {
      ALERT.deleteConfirmAlert("Do you want to remove this fine?")
              .ifPresent(confirmation -> {
                 if (confirmation == ButtonType.OK) {
                    REPO.deleteByUuid(uuidSelected);
                    fines.remove(indexSelected);
                    this.tableFines.getItems().remove(indexSelected);
                 }
              });
      this.btnClear.fire();
   }

   /**
    * Takes an {@link FilteredList} and sets a {@link java.util.function.Predicate} for
    * evaluate the filter on the list and set it to the table.
    * @param keyEvent the Key Event.
    * @param filteredList The list that sets the filtered data.
    */
   @FXML
   private void handleFilter(KeyEvent keyEvent, FilteredList<FineDetailsDto> filteredList) {
      String value = txtFilter.getText().trim();
      filteredList.setPredicate(fine -> {
         if (value.isEmpty()) {
            return true;
         }
         return fine.nameUser().contains(value) ||
                 fine.idUser() == Integer.parseInt(value) ||
                 fine.daysRemaining() == Integer.parseInt(value) ||
                 fine.cause().contains(value);
      });
      this.tableFines.setItems(filteredList);
   }

   /**
    * This set the data before the view load.
    * Takes an id witch going to be used for filter the data
    * and then return a list witch contains all the fines associated
    * with the user and set it to the local, also it calls the method used
    * to configure the table.
    * @param id the user id
    */
   @FXML
   protected void setIdUser(int id) {
      fines = FINES_DETAILS.getJoins()
              .stream()
              .filter(f -> f.idUser() == id)
              .sorted(Comparator.comparing(FineDetailsDto::idUser))
              .collect(Collectors.toList());
      tableConfig();
   }

   /**
    * Set the data before the view load and class
    * the method for configure the table.
    */
   @FXML
   protected void setData() {
      tableConfig();
      fines = FINES_DETAILS.getJoins();
   }

   /**
    * Takes the selection on the table as input and then sets values according to it.
    * @param selection the {@link FineDetailsDto} object selected.
    */
   private void onSelection(FineDetailsDto selection){
      this.uuidSelected = selection.uuid();
      this.indexSelected = tableFines.getSelectionModel().getSelectedIndex();
      this.btnDelete.setDisable(false);
      this.btnUpdate.setDisable(false);
      this.txtNewCause.setText(selection.cause());
   }

   /**
    * Controls the update case for the current selected value in the table, updating
    * the db, list and table record associated.
    * @param f the {@link FineEntity} object.
    */
   private void updateCause(FineEntity f) {
      f.setCause(txtNewCause.getText().trim());
      REPO.update(f);
      FineDetailsDto fine = FineDetailsDto
              .builder()
              .uuid(uuidSelected)
              .nameUser(this.tableFines.getSelectionModel().getSelectedItem().nameUser())
              .idUser(f.getIdUser())
              .cause(f.getCause())
              .daysRemaining(this.tableFines.getSelectionModel().getSelectedItem().daysRemaining())
              .build();
      fines.set(indexSelected, fine);
      this.tableFines.getItems().set(indexSelected, fine);
   }
}