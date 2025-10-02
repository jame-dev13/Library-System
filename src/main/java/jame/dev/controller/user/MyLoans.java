package jame.dev.controller.user;

import jame.dev.dtos.loans.LoanDetailsDto;
import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.LoanService;
import jame.dev.utils.CustomAlert;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MyLoans {

   @FXML
   private Button btnClear;
   @FXML
   private Button btnReturnLoan;
   @FXML
   private TableView<LoanDetailsDto> tableLoans;
   @FXML
   private TableColumn<LoanDetailsDto, UUID> colUuid;
   @FXML
   private TableColumn<LoanDetailsDto, String> colTitle;
   @FXML
   private TableColumn<LoanDetailsDto, String> colAuthor;
   @FXML
   private TableColumn<LoanDetailsDto, EStatusLoan> colStatus;
   @FXML
   private TableColumn<LoanDetailsDto, Integer> colDays;

   private UUID uuidSelected;
   private int indexSelected;
   private static final CRUDRepo<LoanEntity> REPO = new LoanService();
   private static List<LoanDetailsDto> loans;
   private static final CustomAlert ALERT = CustomAlert.getInstance();


   @FXML
   private void initialize() throws IOException {
      loans = new LoanService().getJoinsAll();
      tableConfig();
      this.btnReturnLoan.setOnAction(this::handleReturnLoan);
      this.btnClear.setOnAction(this::handleClear);
   }

   @FXML
   private void tableConfig() {
      //cols
      colUuid.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().uuid()));
      colTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().title()));
      colAuthor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().author()));
      colStatus.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().statusLoan()));
      colDays.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().remainingDays()).asObject());
      //data
      this.tableLoans.setItems(FXCollections.observableArrayList(loans));
      //selection
      this.tableLoans.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      //On click listener
      this.tableLoans.setOnMouseClicked(_ ->
              Optional.ofNullable(this.tableLoans.getSelectionModel().getSelectedItem())
                      .ifPresent(loanDto -> {
                         this.uuidSelected = loanDto.uuid();
                         this.indexSelected = this.tableLoans.getSelectionModel().getSelectedIndex();
                         this.btnReturnLoan.setDisable(false);
                      })
      );
   }

   @FXML
   private void handleReturnLoan(ActionEvent event) {
      REPO.findByUuid(uuidSelected).ifPresentOrElse(loan ->
                      ALERT.buildAlert(Alert.AlertType.CONFIRMATION, "CONFIRMATION", "You want to return this book?")
                              .showAndWait()
                              .ifPresent(confirmation -> {
                                 if (confirmation == ButtonType.OK) {
                                    loan.setStatusLoan(EStatusLoan.RETURNED);
                                    loans.remove(this.indexSelected);
                                    this.tableLoans.getItems().remove(this.indexSelected);
                                    REPO.update(loan);
                                 }
                              }),
              () -> ALERT.buildAlert(Alert.AlertType.WARNING, "NOT FOUND", "Loan not found.").show());
   }

   @FXML private void handleClear(ActionEvent event){
      this.tableLoans.getSelectionModel().clearSelection();
      this.uuidSelected = null;
      this.indexSelected = -1;
      this.btnReturnLoan.setDisable(!this.btnReturnLoan.isDisabled());
   }
}
