package jame.dev.controller.admin;

import jame.dev.dtos.users.UserRunOutLoanDto;
import jame.dev.models.entitys.FineEntity;
import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.FineService;
import jame.dev.service.LoanService;
import jame.dev.utils.CustomAlert;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import lombok.extern.java.Log;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Log
public class Fines {
   //fields
   @FXML
   private TextArea txtCause;
   @FXML
   private TextField txtIdUser;
   @FXML
   private TextField txtFilter;
   @FXML
   private DatePicker dateExpiration;

   //buttons
   @FXML
   private Button btnSave;
   @FXML
   private Button btnClear;
   @FXML
   private Button btnUpdate;
   @FXML
   private Button btnDelete;

   //check buttons
   @FXML
   private CheckBox checkFines;
   @FXML
   private CheckBox checkState;

   //tables
   @FXML
   private TableView<FineEntity> tableFines;
   @FXML
   private TableView<UserRunOutLoanDto> tableStateUser;

   //columns table 'fines'
   @FXML
   private TableColumn<FineEntity, UUID> colUuid;
   @FXML
   private TableColumn<FineEntity, Integer> colIdUser;
   @FXML
   private TableColumn<FineEntity, String> colCause;
   @FXML
   private TableColumn<FineEntity, LocalDate> colExp;

   //columns table 'state users'
   @FXML
   private TableColumn<UserRunOutLoanDto, Integer> colId;
   @FXML
   private TableColumn<UserRunOutLoanDto, EStatusLoan> colStatus;

   //repositories
   private CRUDRepo<FineEntity> fineRepo;
   //local list
   private static List<FineEntity> fines;
   //aux
   private UUID uuidSelected;
   private int indexSelected;
   private FilteredList<?> filteredListFines;

   private static final CustomAlert alert = CustomAlert.getInstance();

   /**
    * Initializes components, global data and listeners, everything of that
    * type must be in this method.
    */
   @FXML
   private void initialize() throws IOException {
      //services
      this.fineRepo = new FineService();
      //global List
      fines = this.fineRepo.getAll();
      //table Fines
      tableFinesConfig();
      tableStateLoanConfig();
      //checkBox listener
      checkFines.selectedProperty().addListener((_, _, isNowSelected) -> {
         if (isNowSelected) {
            checkState.setSelected(false);
            txtFilter.setDisable(false);
            filteredListFines = new FilteredList<>(tableFines.getItems(), _ -> true);
         } else if (!checkState.isSelected()) {
            txtFilter.setDisable(true);
            filteredListFines = null;
         }
      });

      checkState.selectedProperty().addListener((_, _, isNowSelected) -> {
         if (isNowSelected) {
            checkFines.setSelected(false);
            txtFilter.setDisable(false);
            filteredListFines = new FilteredList<>(tableStateUser.getItems(), _ -> true);
         } else if (!checkFines.isSelected()) {
            txtFilter.setDisable(true);
            filteredListFines = null;
         }
      });

      //txtFilter listener
      this.txtFilter.setOnKeyTyped(key -> this.handleFilter(key, filteredListFines));

      //buton listeners
      this.btnClear.setOnAction(this::handleClear);
      this.btnSave.setOnAction(this::handleSaveFine);
      this.btnUpdate.setOnAction(this::handleUpdateFine);
      this.btnDelete.setOnAction(this::handleDeleteFine);
   }

   /**
    * Defines the background configuration for the TableView
    * witch includes data, selection and listeners options.
    */
   @FXML
   private void tableFinesConfig() {
      //columns
      this.colUuid.setCellValueFactory(new PropertyValueFactory<>("uuid"));
      this.colIdUser.setCellValueFactory(new PropertyValueFactory<>("idUser"));
      this.colCause.setCellValueFactory(new PropertyValueFactory<>("cause"));
      this.colExp.setCellValueFactory(new PropertyValueFactory<>("expiration"));
      //data
      ObservableList<FineEntity> observableList = FXCollections.observableArrayList(fines);
      this.tableFines.setItems(observableList);
      //selection
      this.tableFines.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      //listeners
      this.tableFines.setOnMouseClicked(_ ->
              Optional.ofNullable(this.tableFines.getSelectionModel().getSelectedItem())
                      .ifPresent(selection -> {
                         this.uuidSelected = selection.getUuid();
                         this.indexSelected = this.tableFines.getSelectionModel().getSelectedIndex();
                         this.btnUpdate.setDisable(false);
                         this.btnDelete.setDisable(false);
                      })
      );
   }

   /**
    * Defines the background configuration for the TableView
    * witch includes data, selection and listeners options.
    */
   @FXML
   private void tableStateLoanConfig() {
      //columns
      this.colId.setCellValueFactory(data ->
              new SimpleIntegerProperty(data.getValue().idUser()).asObject());
      this.colStatus.setCellValueFactory(data ->
              new SimpleObjectProperty<>(data.getValue().status()));
      //data
      List<UserRunOutLoanDto> runOutList = new LoanService().getAll()
              .stream()
              .map(l -> UserRunOutLoanDto.builder()
                      .idUser(l.getIdUser())
                      .status(l.getStatusLoan())
                      .build())
              .distinct()
              .toList();
      ObservableList<UserRunOutLoanDto> observableList = FXCollections.observableArrayList(runOutList);
      this.tableStateUser.setItems(observableList);
      //Selection
      this.tableStateUser.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      //listener
      this.tableStateUser.setOnMouseClicked(_ ->
              Optional.ofNullable(this.tableStateUser.getSelectionModel().getSelectedItem())
                      .ifPresent(selection ->
                              this.txtIdUser.setText(String.valueOf(selection.idUser()))
                      ));
   }

   /**
    * Manages the clean data for the view like:
    * text, selections and/or enable/disable buttons.
    */
   @FXML
   private void handleClear(ActionEvent event) {
      //clear fields
      this.txtIdUser.clear();
      this.txtCause.clear();
      this.dateExpiration.setValue(null);
      this.txtFilter.clear();
      //clear selections
      this.tableFines.getSelectionModel().clearSelection();
      this.tableStateUser.getSelectionModel().clearSelection();
      //disable buttons
      this.btnUpdate.setDisable(!this.btnUpdate.isDisabled());
      this.btnDelete.setDisable(!this.btnDelete.isDisabled());
      //disable checks
      this.checkFines.setSelected(!this.checkFines.isSelected());
      this.checkState.setSelected(!this.checkState.isSelected());
      //resets globals
      this.uuidSelected = null;
      this.indexSelected = -1;
   }

   /***
    * Manages the build of data witch is going to be saved
    * by the repository, also it updates the local ArrayList and sends a communication username
    * to the new user, finally fire the clear button and shows an {@link CustomAlert}
    * for confirmation or error.
    */
   @FXML
   private void handleSaveFine(ActionEvent event) {
      try {
         FineEntity fine = FineEntity.builder()
                 .uuid(UUID.randomUUID())
                 .idUser(Integer.parseInt(txtIdUser.getText()))
                 .cause(txtCause.getText().trim())
                 .expiration(dateExpiration.getValue())
                 .build();
         this.fineRepo.save(fine);
         fines.add(fine);
         this.tableFines.getItems().add(fine);
         alert.buildAlert(Alert.AlertType.CONFIRMATION, "CONFIRMATION", "Fine saved!")
                 .show();
      } catch (NullPointerException e) {
         alert.buildAlert(Alert.AlertType.ERROR, "ERROR", "Fields Can´t contain null or empty values.")
                 .show();
         log.severe(e.toString());
      } finally {
         this.btnClear.fire();
      }
   }

   /**
    * Handles the logic for do and update if and UUID value is present.
    *
    * @param event event
    * @throws NullPointerException if some value from {@link FineEntity} is null
    */
   @FXML
   private void handleUpdateFine(ActionEvent event) {
      try {
         this.fineRepo.findByUuid(uuidSelected)
                 .ifPresent(fine -> {
                    //set Values
                    fine.setCause(txtCause.getText().trim());
                    fine.setExpiration(dateExpiration.getValue());
                    //save DB
                    this.fineRepo.update(fine);
                    //update lists
                    fines.set(indexSelected, fine);
                    this.tableFines.getItems().set(indexSelected, fine);
                 });
         alert.buildAlert(Alert.AlertType.CONFIRMATION, "CONFIRMATION", "Fine updated!")
                 .show();
      } catch (NullPointerException e) {
         alert.buildAlert(Alert.AlertType.ERROR, "ERROR", "Fields Can´t contain null or empty values.")
                 .show();
         log.severe(e.toString());
      } finally {
         this.btnClear.fire();
      }
   }

   /**
    * Manages the delete logic for a user in the view and the background
    * based on the selected uuid and index, checks if the selected
    * row info doesn't match with the session data then, it applies
    * the deletion logic.
    *
    * @param event ActionEvent
    */
   @FXML
   private void handleDeleteFine(ActionEvent event) {
      alert.buildAlert(Alert.AlertType.WARNING, "DELETE",
                      "¿Do you want delete this fine?")
              .showAndWait()
              .ifPresent(confirmation -> {
                 if (confirmation == ButtonType.OK) {
                    this.fineRepo.deleteByUuid(uuidSelected);
                    fines.remove(indexSelected);
                    this.tableFines.getItems().remove(indexSelected);
                 }
              });
      this.btnClear.fire();
   }

   /**
    * Manages two predicates for the type of filter selected, and applies
    * it determining the type of the filtered list.
    *
    * @param keyEvent     event
    * @param filteredList list for do the filter
    * @throws IllegalArgumentException if the text value does not match with the value of {@link EStatusLoan}
    * @throws NullPointerException     if the list is null
    */

   @FXML
   private void handleFilter(KeyEvent keyEvent, FilteredList<?> filteredList) {
      String text = txtFilter.getText().trim();
      Predicate<FineEntity> predicateFines = fine -> {
         if (text.isEmpty()) return true;
         return fine.getUuid().toString().contains(text) ||
                 String.valueOf(fine.getId()).contains(text) ||
                 fine.getCause().contains(text) ||
                 fine.getExpiration().toString().contains(text);
      };

      Predicate<LoanEntity> predicateLoans = loan -> {
         if (text.isEmpty()) return true;
         return String.valueOf(loan.getIdUser()).contains(text) ||
                 loan.getStatusLoan() == EStatusLoan.valueOf(text.toUpperCase());
      };

      try {
         //set predicate
         Optional.ofNullable(filteredList).ifPresentOrElse(list ->
                 list.setPredicate(type -> {
                    if (type instanceof FineEntity fine) return predicateFines.test(fine);
                    if (type instanceof LoanEntity loan) return predicateLoans.test(loan);
                    return true;
                 }), () -> {
            throw new NullPointerException("List is not present");
         });
      } catch (IllegalArgumentException e) {
         alert.buildAlert(Alert.AlertType.ERROR, "ERROR", "Value not defined.")
                 .show();
      }
   }

}