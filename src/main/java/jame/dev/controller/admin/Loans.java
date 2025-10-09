package jame.dev.controller.admin;

import jame.dev.models.entitys.FineEntity;
import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.CRUDRepo;
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
import java.util.*;
import java.util.stream.Collectors;

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
   private static List<LoanEntity> loans;
   private static Set<Integer> IDS = new HashSet<>();
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
      IDS = fineService.getAll()
              .stream()
              .map(FineEntity::getIdUser)
              .collect(Collectors.toSet());
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
         if (clicks == 2) {
            repo.findByUuid(uuidSelected)
                    .ifPresentOrElse(l -> loadModalFines(l.getIdUser()),
                            () -> alert.buildAlert(Alert.AlertType.INFORMATION, "INFO", "No value to load present.")
                                    .show());
         }
      });
   }

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

   @FXML
   private void handleSet(ActionEvent event) {
      this.repo.findByUuid(uuidSelected)
              .ifPresent(this::update);
      this.btnClear.fire();
   }

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
         Platform.runLater(() -> new Label("Error loading modal."));
         log.severe(e.getMessage());
      }
   }

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
         Platform.runLater(() -> new Label("Error loading modal."));
         log.severe(e.getMessage());
      }
   }

   private void updateStatusOnLoad(List<LoanEntity> l){
      l.stream()
              .filter(loan -> LocalDate.now().isAfter(loan.getReturnDate())
                      && loan.getStatusLoan() == EStatusLoan.ON_LOAN)
              .forEach(loan -> {
                 loan.setStatusLoan(EStatusLoan.RUN_OUT);
                 this.repo.update(loan);
              });
   }

   private void onSelection(LoanEntity selection){
      this.uuidSelected = selection.getUuid();
      this.indexSelected = this.tableLoans.getSelectionModel().getSelectedIndex();
      this.dateLoan.setValue(selection.getLoanDate());
      this.dateReturn.setValue(selection.getReturnDate());
      this.btnSet.setDisable(false);
      this.btnDelete.setDisable(false);
      if (selection.getStatusLoan() == EStatusLoan.RUN_OUT) btnFine.setDisable(false);
   }

   private void save(LoanEntity entity){
      if (!IDS.add(entity.getIdUser())) {
         alert.buildAlert(Alert.AlertType.ERROR, "ERROR", "This user is fined already.")
                 .show();
         return;
      }
      FineEntity fine = FineEntity.builder()
              .uuid(UUID.randomUUID())
              .idUser(entity.getIdUser())
              .cause("Didn't return the loan on time.")
              .expiration(LocalDate.now().plusDays(15))
              .build();
      fineService.save(fine);
      alert.buildAlert(Alert.AlertType.INFORMATION, "INFO", "User fined.")
              .show();
      entity.setStatusLoan(EStatusLoan.FINED);
      this.repo.update(entity);
      loans.set(indexSelected, entity);
      this.tableLoans.getItems().set(indexSelected, entity);
   }

   private void update(LoanEntity loan){
      loan.setLoanDate(dateLoan.getValue());
      loan.setReturnDate(dateReturn.getValue());
      this.repo.update(loan);
      this.tableLoans.getItems().set(indexSelected, loan);
      loans.set(indexSelected, loan);
      alert.buildAlert(Alert.AlertType.INFORMATION, "UPDATED", "Record updated!")
              .show();
   }

   private void delete(){
      EStatusLoan statusSelected = loans.get(indexSelected).getStatusLoan();
      if (statusSelected == EStatusLoan.ON_LOAN || statusSelected == EStatusLoan.RENEWED) {
         alert.buildAlert(Alert.AlertType.ERROR, "NOT ALLOWED", "This is a temporary valid loan that can't be removed yet.")
                 .show();
         return;
      }
      this.repo.deleteByUuid(uuidSelected);
      loans.remove(indexSelected);
      this.tableLoans.getItems().remove(indexSelected);
   }
}
