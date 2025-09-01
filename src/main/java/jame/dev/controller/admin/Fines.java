package jame.dev.controller.admin;

import jame.dev.models.entitys.FineEntity;
import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.FineService;
import jame.dev.service.LoanService;
import jame.dev.utils.CustomAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class Fines {
   //fields
   @FXML private TextArea txtCause;
   @FXML private TextField txtIdUser;
   @FXML private TextField txtFilter;
   @FXML private DatePicker dateExpiration;

   //buttons
   @FXML private Button btnSave;
   @FXML private Button btnClear;
   @FXML private Button btnUpdate;
   @FXML private Button btnDelete;

   //check buttons
   @FXML private CheckBox checkFines;
   @FXML private CheckBox checkState;

   //tables
   @FXML private TableView<FineEntity> tableFines;
   @FXML private TableView<LoanEntity> tableStateUser;

   //columns table 'fines'
   @FXML private TableColumn<FineEntity, UUID> colUuid;
   @FXML private TableColumn<FineEntity, Integer> colIdUser;
   @FXML private TableColumn<FineEntity, String> colCause;
   @FXML private TableColumn<FineEntity, LocalDate> colExp;

   //columns table 'state users'
   @FXML private TableColumn<LoanEntity, Integer> colId;
   @FXML private TableColumn<LoanEntity, EStatusLoan> colStatus;

   private CRUDRepo<FineEntity> fineRepo;
   private CRUDRepo<LoanEntity> loanRepo;
   private static List<FineEntity> fines;
   private static List<LoanService> loans;
   private int idUserSelected;
   private UUID uuidSelected;
   private int indexSelected;
   private FilteredList<?> filteredListFines;

   @FXML private void initialize(){
      //services
      this.fineRepo = new FineService();
      this.loanRepo = new LoanService();
      //global List
      fines = this.fineRepo.getAll();
      //table Fines
      tableFinesConfig();
      //set filtered data, check and txtFilter listeners
      checkFines.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
         if (isNowSelected) {
            checkState.setSelected(false);
            txtFilter.setDisable(false);
            filteredListFines = new FilteredList<>(tableFines.getItems(), p -> true);
         } else if (!checkState.isSelected()) {
            txtFilter.setDisable(true);
            filteredListFines = null;
         }
      });

      checkState.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
         if (isNowSelected) {
            checkFines.setSelected(false);
            txtFilter.setDisable(false);
            filteredListFines = new FilteredList<>(tableStateUser.getItems(), p -> true);
         } else if (!checkFines.isSelected()) {
            txtFilter.setDisable(true);
            filteredListFines = null;
         }
      });

      this.txtFilter.setOnKeyTyped(key -> this.handleFilter(key, filteredListFines));

      //buton listeners
      this.btnClear.setOnAction(this::handleClear);
      this.btnSave.setOnAction(this::handleSaveFine);
      this.btnUpdate.setOnAction(this::handleUpdateFine);
      this.btnDelete.setOnAction(this::handleDeleteFine);
   }

   @FXML private void tableFinesConfig(){
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
      this.tableFines.setOnMouseClicked(m -> {
         Optional.ofNullable(this.tableFines.getSelectionModel().getSelectedItem())
                 .ifPresent(selection -> {
                    this.uuidSelected = selection.getUuid();
                    this.indexSelected = this.tableFines.getSelectionModel().getSelectedIndex();
                    this.btnUpdate.setDisable(false);
                    this.btnDelete.setDisable(false);
                 });
      });
   }

   @FXML private void handleClear(ActionEvent event){
      this.txtIdUser.clear();
      this.txtCause.clear();
      this.dateExpiration.setValue(null);
      this.txtFilter.clear();
      this.tableFines.getSelectionModel().clearSelection();
      this.tableStateUser.getSelectionModel().clearSelection();
      if(!btnDelete.isDisabled() && !btnUpdate.isDisabled()){
         this.btnUpdate.setDisable(true);
         this.btnDelete.setDisable(true);
      }
      if(this.checkFines.isSelected() || this.checkState.isSelected()){
         this.checkState.setSelected(false);
         this.checkFines.setSelected(false);
      }

      this.uuidSelected = null;
      this.indexSelected = -1;
   }

   @FXML private void handleSaveFine(ActionEvent event){
      try {
         FineEntity fine = FineEntity.builder()
                 .uuid(UUID.randomUUID())
                 .id(Integer.parseInt(txtIdUser.getText()))
                 .cause(txtCause.getText().trim())
                 .expiration(dateExpiration.getValue())
                 .build();
         this.fineRepo.save(fine);
         fines.add(fine);
         this.tableFines.getItems().add(fine);
         CustomAlert.getInstance()
                 .buildAlert(Alert.AlertType.CONFIRMATION, "CONFIRMATION", "Fine saved!")
                 .show();
      }catch (NullPointerException e){
         CustomAlert.getInstance()
                 .buildAlert(Alert.AlertType.ERROR, "ERROR", "Fields Can´t contain null or empty values.")
                 .show();
         throw new RuntimeException(e);
      }
   }

   @FXML private void handleUpdateFine(ActionEvent event){
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
         CustomAlert.getInstance()
                 .buildAlert(Alert.AlertType.CONFIRMATION, "CONFIRMATION", "Fine updated!")
                 .show();
      }catch (NullPointerException e){
         CustomAlert.getInstance()
                 .buildAlert(Alert.AlertType.ERROR, "ERROR", "Fields Can´t contain null or empty values.")
                 .show();
         throw new RuntimeException(e);
      }
   }

   @FXML private void handleDeleteFine(ActionEvent event){
        CustomAlert.getInstance()
                .buildAlert(Alert.AlertType.WARNING, "DELETE",
                        "¿Do you want delete this fine?")
                .showAndWait()
                .ifPresent(confirmation -> {
                   if(confirmation == ButtonType.OK){
                      this.fineRepo.deleteByUuid(uuidSelected);
                      fines.remove(indexSelected);
                      this.tableFines.getItems().remove(indexSelected);
                   }
                });
   }

   @FXML private void handleFilter(KeyEvent keyEvent,  FilteredList<?> filteredList){
      boolean isFinesCheckActive = checkFines.isSelected();
      String text = txtFilter.getText().trim();
      Predicate<FineEntity> predicateFines = fine -> {
         if(text.isEmpty()) return true;
         return fine.getUuid().toString().contains(text) ||
                 String.valueOf(fine.getId()).contains(text) ||
                 fine.getCause().contains(text) ||
                 fine.getExpiration().toString().contains(text);
      };

      Predicate<LoanEntity> predicateLoans = loan -> {
        if(text.isEmpty()) return true;
        return String.valueOf(loan.getIdUser()).contains(text) ||
                loan.getStatusLoan() == EStatusLoan.valueOf(text.toUpperCase());
      };

      try{
         //set predicate
         Optional.ofNullable(filteredList).ifPresentOrElse(list -> {
            list.setPredicate(type -> {
               if(type instanceof FineEntity fine) return predicateFines.test(fine);
               if(type instanceof LoanEntity loan) return predicateLoans.test(loan);
               return true;
            });
         }, () -> {throw new NullPointerException("Null list is present");});
      }catch (IllegalArgumentException e){
         throw new RuntimeException(e);
      }
   }
}