package jame.dev.controller.admin;

import jame.dev.models.entitys.CopyEntity;
import jame.dev.models.entitys.FineEntity;
import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.CopyService;
import jame.dev.service.FineService;
import jame.dev.service.LoanService;
import jame.dev.utils.ui.CustomAlert;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.java.Log;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller class that gives functionality to the view of Loans.
 * @author jame-dev13
 */
@Log
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
   @FXML
   private Button btnShowFines;
   //Date Pickers
   @FXML
   private DatePicker dateLoan;
   @FXML
   private DatePicker dateReturn;
   //Fields
   @FXML
   private TextField txtFilter;

   private final CRUDRepo<LoanEntity> repo = new LoanService();
   private static final CRUDRepo<CopyEntity> COPIES_REPO = new CopyService();
   private static List<CopyEntity> copies;
   private static List<LoanEntity> loans;
   private static final FineService fineService = new FineService();
   private UUID uuidSelected;
   private int indexSelected;
   private static final CustomAlert alert = CustomAlert.getInstance();

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
      copies = COPIES_REPO.getAll();
      //table
      tableConfig();

      //buttons listeners
      btnClear.setOnAction(this::handleClear);
      btnSet.setOnAction(this::handleSet);
      btnDelete.setOnAction(this::handleDelete);
      btnFine.setOnAction(this::handleFine);
      btnShowFines.setOnAction(this::loadModalFines);

      //txtFilter listener
      FilteredList<LoanEntity> filteredList = new FilteredList<>(this.tableLoans.getItems(), p -> true);
      txtFilter.setOnKeyTyped(key -> this.handleFilter(key, filteredList));

      //update status loan on expired date.
      Optional.ofNullable(loans)
              .ifPresent(this::updateStatusOnLoad);
   }

   /**
    * Method to set Columns and Table properties like, dataType, selection, listeners and data.
    */
   @FXML
   private void tableConfig() {
      //columns
      this.colUuid.setCellValueFactory(data ->
              new SimpleObjectProperty<>(data.getValue().getUuid()));
      this.colIdUser.setCellValueFactory(data ->
              new SimpleIntegerProperty(data.getValue().getIdUser()).asObject());
      this.colIdCopy.setCellValueFactory(data ->
              new SimpleIntegerProperty(data.getValue().getIdCopy()).asObject());
      this.colLoanDate.setCellValueFactory(data ->
              new SimpleObjectProperty<>(data.getValue().getLoanDate()));
      this.colReturnDate.setCellValueFactory(data ->
              new SimpleObjectProperty<>(data.getValue().getReturnDate()));
      this.colStatus.setCellValueFactory(data ->
              new SimpleObjectProperty<>(data.getValue().getStatusLoan()));

      //table config
      this.tableLoans.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      ObservableList<LoanEntity> observableList = FXCollections.observableArrayList(loans);
      this.tableLoans.setItems(observableList);

      //listeners
      this.tableLoans.setOnMouseClicked(m -> {
         LoanEntity selected = this.tableLoans.getSelectionModel().getSelectedItem();
         Optional.ofNullable(selected).ifPresent(this::onSelection);
         int clicks = m.getClickCount();
         //two clicks for load modal to see the fines associated with the user.
         if (clicks == 2) {
            repo.findByUuid(uuidSelected)
                    .ifPresentOrElse(l -> loadModalFines(l.getIdUser()),
                            () -> alert.buildAlert(Alert.AlertType.INFORMATION, "INFO", "No value to load present.")
                                    .show());
         }
      });
   }

   /**
    * Cleans up the fields, selection, and enable/disable components.
    * @param event
    */
   @FXML
   private void handleClear(ActionEvent event) {
      //fields
      this.dateLoan.setValue(null);
      this.dateReturn.setValue(null);
      //selection
      this.tableLoans.getSelectionModel().clearSelection();
      //disable buttons
      this.btnDelete.setDisable(true);
      this.btnSet.setDisable(true);
      this.btnFine.setDisable(true);
      //reset globals
      this.uuidSelected = null;
      this.indexSelected = -1;
   }

   /**
    * Listener method to Fine a user.
    * @param event the ActionEvent.
    */
   @FXML
   private void handleFine(ActionEvent event) {
      alert.buildAlert(Alert.AlertType.CONFIRMATION, "CONFIRMATION", "Do you want fine this user?")
              .showAndWait()
              .ifPresent(confirmation -> {
                 if (confirmation == ButtonType.OK) {
                    Optional.ofNullable(this.tableLoans.getSelectionModel().getSelectedItem())
                            .ifPresent(this::save);
                 }
              });
      this.btnClear.fire();
   }

   /**
    * Set the new data for the current selected object.
    * @param event the ActionEvent.
    */
   @FXML
   private void handleSet(ActionEvent event) {
      this.repo.findByUuid(uuidSelected)
              .ifPresent(this::update);
      this.btnClear.fire();
   }

   /**
    * Delete a loan {@link LoanEntity} object
    * @param event the ActionEvent.
    */
   @FXML
   private void handleDelete(ActionEvent event) {
      alert.buildAlert(Alert.AlertType.CONFIRMATION,
                      "CONFIRMATION",
                      "¿Do you want to delete this record?")
              .showAndWait()
              .ifPresent(confirmation -> {
                 if (confirmation == ButtonType.OK) {
                    this.delete();
                 }
              });
      this.btnClear.fire();
   }

   /**
    * Filter data into the table taking the TextField text present for input,
    * then on reference to it a predicate is going to be evaluated and finally
    * the table going to set his own data on reference this FilteredList.
    * @param event the KeyEvent.
    * @param filteredList the {@link FilteredList} that going to be set.
    */
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
         alert.errorAlert( "Value is not defined.");
         log.severe(e.getMessage());
      }
   }

   /**
    * Tries to load the stage using like a modal to see the fines associated
    * with a user using his id to search it.
    * @param id the {@link LoanEntity} idUser property
    */
   @FXML
   private void loadModalFines(int id) {
      try {
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/adminPanes/fines.fxml"));
         Parent root = loader.load();

         Fines controller = loader.getController();
         controller.setIdUser(id);

         Stage stage = new Stage();
         stage.setTitle("User Fines");
         stage.setScene(new Scene(root));
         stage.resizableProperty().set(false);
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.showAndWait();
      } catch (IOException e) {
         Platform.runLater(() -> alert.errorAlert("Error loading modal."));
         log.severe(e.getMessage());
      }
   }

   /**
    * Loads a stage using it like a modal to see all the registed fines.
    * @param event the Action Event.
    */
   @FXML
   private void loadModalFines(ActionEvent event) {
      try {
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/adminPanes/fines.fxml"));
         Parent root = loader.load();

         Fines controller = loader.getController();
         controller.setData();

         Stage stage = new Stage();
         stage.setTitle("Fines");
         stage.setScene(new Scene(root));
         stage.resizableProperty().set(false);
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.showAndWait();
      } catch (IOException e) {
         Platform.runLater(() -> alert.errorAlert("Error loading modal."));
         log.severe(e.getMessage());
      }
   }

   /**
    * On the load, it will update the property statusLoan of a {@link LoanEntity} object if
    * the Localdate.now().ifAfter(LoanEntity.getReturnDate()) gets true.
    * The update is in reference to {@link EStatusLoan}, more specifically to:
    * {@code EStatusLoan.RUN_OUT} if the given statusLoan is equal to {@code EStatusLoan.ON_LOAN} too.
    * @param l the {@link List} of LoanEntities.
    */
   private void updateStatusOnLoad(List<LoanEntity> l){
      l.stream()
              .filter(loan -> LocalDate.now().isAfter(loan.getReturnDate())
                      && loan.getStatusLoan() == EStatusLoan.ON_LOAN)
              .forEach(loan -> {
                 loan.setStatusLoan(EStatusLoan.RUN_OUT);
                 this.repo.update(loan);
              });
   }

   /**
    * Set some values on relation with the current selection
    * @param selection the {@link LoanEntity} object
    */
   private void onSelection(LoanEntity selection){
      this.uuidSelected = selection.getUuid();
      this.indexSelected = this.tableLoans.getSelectionModel().getSelectedIndex();
      this.dateLoan.setValue(selection.getLoanDate());
      this.dateReturn.setValue(selection.getReturnDate());
      this.btnSet.setDisable(false);
      this.btnDelete.setDisable(false);
      if (selection.getStatusLoan() == EStatusLoan.RUN_OUT) btnFine.setDisable(false);
   }

   /**
    * Logic to save a FineEntity object.
    * This a built-in action generates a Fine in just a click,
    * build the Entity and save it into the db, an updates the given {@link LoanEntity}
    * on the view with the {@link EStatusLoan} of {@code EStatusLoan.FINED}
    * @param entity the {@link LoanEntity} object.
    */
   private void save(LoanEntity entity){
      FineEntity fine = FineEntity.builder()
              .uuid(UUID.randomUUID())
              .idUser(entity.getIdUser())
              .cause("Didn't return the loan on time.")
              .expiration(LocalDate.now().plusDays(15))
              .build();
      fineService.save(fine);
      alert.infoAlert( "User fined.");
      entity.setStatusLoan(EStatusLoan.FINED);
      this.repo.update(entity);
      loans.set(indexSelected, entity);
      this.tableLoans.getItems().set(indexSelected, entity);
      //release the copy borrowed
      this.releaseCopy(entity);
   }

   /**
    * {@link LoanEntity} object can only change his loanDate and returnDate property
    * This evaluates that the difference between both dates is less than 60, only in that
    * case the update is valid, otherwise an alert notifies the user.
    * @param loan the {@link LoanEntity} object.
    */
   private void update(LoanEntity loan){
      LocalDate newDate = dateLoan.getValue();
      LocalDate newReturn = dateReturn.getValue();
      int diff = (int) ChronoUnit.DAYS.between(newReturn, newDate);
      if(diff > 60){
         alert.warningAlert("The maximum of days for a Loan is 60 days.");
         return;
      }
      loan.setLoanDate(dateLoan.getValue());
      loan.setReturnDate(dateReturn.getValue());
      this.repo.update(loan);
      this.tableLoans.getItems().set(indexSelected, loan);
      loans.set(indexSelected, loan);
      alert.infoAlert( "Record updated!");
   }

   /**
    * Removes from the view, memory and db a not valid temporary Loan, a Loan is temporary
    * valid if his status is Equal to {@code EStatusLoan.ON_LOAN or EStatusLoan.RENEWED}
    */
   private void delete(){
      EStatusLoan statusSelected = loans.get(indexSelected).getStatusLoan();
      if (statusSelected == EStatusLoan.ON_LOAN || statusSelected == EStatusLoan.RENEWED) {
         alert.warningAlert("This is a temporary valid loan that can't be removed yet.");
         return;
      }
      this.repo.deleteByUuid(uuidSelected);
      loans.remove(indexSelected);
      LoanEntity loan = this.tableLoans.getItems().remove(indexSelected);
      releaseCopy(loan);
   }

   /**
    * if a user is fined then the copy associated with his loan it's going to be released.
    * @param loan the {@link LoanEntity} object.
    */
   private void releaseCopy(LoanEntity loan){
      copies.stream()
              .filter(c -> c.getId() == loan.getIdCopy())
              .peek(c -> c.setBorrowed(false))
              .findFirst()
              .ifPresent(COPIES_REPO::update);
   }
}
