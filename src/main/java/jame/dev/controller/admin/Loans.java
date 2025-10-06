package jame.dev.controller.admin;

import jame.dev.models.entitys.FineEntity;
import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.FineService;
import jame.dev.service.LoanService;
import jame.dev.utils.CustomAlert;
import jame.dev.utils.EGlobalNames;
import jame.dev.utils.GlobalNotificationChange;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Loans {

   @FXML
   private Label labelDateToday;
   //Table
   @FXML
   private TableView<LoanEntity> tableLoans;
   @FXML
   private TableColumn<LoanEntity, UUID> colUuid;
   @FXML
   private TableColumn<LoanEntity, Integer> colIdUser;
   @FXML
   private TableColumn<LoanEntity, Integer> colIdCopy;
   @FXML
   private TableColumn<LoanEntity, LocalDate> colLoanDate;
   @FXML
   private TableColumn<LoanEntity, LocalDate> colReturnDate;
   @FXML
   private TableColumn<LoanEntity, EStatusLoan> colStatus;
   //buttons
   @FXML
   private Button btnSet;
   @FXML
   private Button btnDelete;
   @FXML
   private Button btnClear;

   @FXML
   private Button btnFine;
   //Date Pickers
   @FXML
   private DatePicker dateLoan;
   @FXML
   private DatePicker dateReturn;
   //Fields
   @FXML
   private TextField txtFilter;

   private final CRUDRepo<LoanEntity> repo = new LoanService();
   private static List<LoanEntity> loans;
   private static final FineService fineService = new FineService();
   private UUID uuidSelected;
   private int indexSelected;
   private static final CustomAlert alert = CustomAlert.getInstance();
   private static final GlobalNotificationChange changes = GlobalNotificationChange.getInstance();
   private static final String changeName = EGlobalNames.FINE_ADMIN.name();

   /**
    * Initializes components, global data and listeners, everything of that
    * type must be in this method.
    */
   @FXML
   private void initialize() throws IOException {
      //label date
      this.labelDateToday.setText(LocalDate.now().toString());
      //loan data
      loans = repo.getAll();
      //table
      tableConfig();

      //buttons listeners
      btnClear.setOnAction(this::handleClear);
      btnSet.setOnAction(this::handleSet);
      btnDelete.setOnAction(this::handleDelete);
      btnFine.setOnAction(this::handleFine);

      //txtFilter listener
      FilteredList<LoanEntity> filteredList = new FilteredList<>(this.tableLoans.getItems(), p -> true);
      txtFilter.setOnKeyTyped(key -> this.handleFilter(key, filteredList));

      //update status loan on expired date.
      Optional.ofNullable(loans)
              .ifPresent(l ->
                      l.stream()
                              .filter(loan -> LocalDate.now().isAfter(loan.getReturnDate())
                                      && loan.getStatusLoan() == EStatusLoan.ON_LOAN)
                              .forEach(loan -> {
                                 loan.setStatusLoan(EStatusLoan.RUN_OUT);
                                 this.repo.update(loan);
                              })
              );
   }

   @FXML
   private void tableConfig() {
      //columns
      this.colUuid.setCellValueFactory(new PropertyValueFactory<>("uuid"));
      this.colIdUser.setCellValueFactory(new PropertyValueFactory<>("idUser"));
      this.colIdCopy.setCellValueFactory(new PropertyValueFactory<>("idCopy"));
      this.colLoanDate.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
      this.colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
      this.colStatus.setCellValueFactory(new PropertyValueFactory<>("statusLoan"));

      //table config
      this.tableLoans.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      ObservableList<LoanEntity> observableList = FXCollections.observableArrayList(loans);
      this.tableLoans.setItems(observableList);

      //listeners
      this.tableLoans.setOnMouseClicked(_ ->
              Optional.ofNullable(this.tableLoans.getSelectionModel().getSelectedItem())
                      .ifPresent(selection -> {
                         this.uuidSelected = selection.getUuid();
                         this.indexSelected = this.tableLoans.getSelectionModel().getSelectedIndex();
                         this.dateLoan.setValue(selection.getLoanDate());
                         this.dateReturn.setValue(selection.getReturnDate());
                         this.btnSet.setDisable(false);
                         this.btnDelete.setDisable(false);
                      })
      );
   }

   @FXML
   private void handleClear(ActionEvent event) {
      //fields
      this.dateLoan.setValue(null);
      this.dateReturn.setValue(null);
      //selection
      this.tableLoans.getSelectionModel().clearSelection();
      //disable buttons
      if (!btnDelete.isDisabled() && !btnSet.isDisabled()) {
         this.btnDelete.setDisable(true);
         this.btnSet.setDisable(true);
      }
      //reset globals
      this.uuidSelected = null;
      this.indexSelected = -1;
   }

   @FXML
   private void handleSet(ActionEvent event) {
      this.repo.findByUuid(uuidSelected)
              .ifPresent(loan -> {
                 loan.setLoanDate(dateLoan.getValue());
                 loan.setReturnDate(dateLoan.getValue());
                 this.repo.update(loan);
                 this.tableLoans.getItems().set(indexSelected, loan);
                 loans.set(indexSelected, loan);
                 alert.buildAlert(Alert.AlertType.INFORMATION, "UPDATED", "Record updated!")
                         .show();
              });
      this.btnClear.fire();
   }

   @FXML
   private void handleDelete(ActionEvent event) {
      Optional.ofNullable(uuidSelected)
              .ifPresent(uuid -> {
                 //add an alert for confirmation
                 alert.buildAlert(Alert.AlertType.CONFIRMATION,
                                 "CONFIRMATION",
                                 "¿Do you want to delete this record?")
                         .showAndWait()
                         .ifPresent(confirmation -> {
                            if (confirmation == ButtonType.OK) {
                               this.repo.deleteByUuid(uuid);
                               loans.remove(indexSelected);
                               this.tableLoans.getItems().remove(indexSelected);
                            }
                         });
              });
      this.btnClear.fire();
   }

   @FXML
   private void handleFine(ActionEvent event) {
      Set<Integer> idUsers = fineService.getAll()
              .stream()
              .map(FineEntity::getIdUser)
              .collect(Collectors.toSet());
      alert.buildAlert(Alert.AlertType.CONFIRMATION, "CONFIRMATION", "Do you want fine this user?")
              .showAndWait()
              .ifPresent(confirmation -> {
                 if (confirmation == ButtonType.OK) {
                    Optional.ofNullable(this.tableLoans.getSelectionModel().getSelectedItem())
                            .ifPresent(entity -> {
                               if (idUsers.add(entity.getIdUser())) {
                                  fineService.save(
                                          FineEntity.builder()
                                                  .uuid(UUID.randomUUID())
                                                  .idUser(entity.getIdUser())
                                                  .cause("Don't return the loan on time.")
                                                  .expiration(LocalDate.now().plusDays(15))
                                                  .build()
                                  );
                                  alert.buildAlert(Alert.AlertType.INFORMATION, "INFO", "User fined.")
                                          .show();
                                  entity.setStatusLoan(EStatusLoan.FINED);
                                  this.repo.update(entity);
                                  loans.set(indexSelected, entity);
                                  this.tableLoans.getItems().set(indexSelected, entity);
                                  changes.registerChange(changeName);
                               } else
                                  alert.buildAlert(Alert.AlertType.ERROR, "ERROR", "This user is fined already.")
                                          .show();
                            });
                 }
              });
   }

   @FXML
   private void handleFilter(KeyEvent event, FilteredList<LoanEntity> filteredList) {
      try {
         String text = txtFilter.getText();
         filteredList.setPredicate(loan -> {
            if (text.isEmpty()) return true;
            return loan.getUuid().toString().contains(text) ||
                    loan.getLoanDate().toString().contains(text) ||
                    loan.getReturnDate().toString().contains(text) ||
                    loan.getStatusLoan() == EStatusLoan.valueOf(text.toUpperCase());
         });
      } catch (IllegalArgumentException e) {
         alert.buildAlert(Alert.AlertType.ERROR, "ERROR", "Value is not defined.").show();
      }
   }
}
