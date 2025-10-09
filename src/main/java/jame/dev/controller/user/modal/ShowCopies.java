package jame.dev.controller.user.modal;

import jame.dev.dtos.copies.CopyDetailsDto;
import jame.dev.models.entitys.CopyEntity;
import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.EGenre;
import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.repositorys.Joinable;
import jame.dev.service.CopyService;
import jame.dev.service.LoanService;
import jame.dev.service.joins.CopyDetailsService;
import jame.dev.utils.db.CheckFinesUtil;
import jame.dev.utils.session.EGlobalNames;
import jame.dev.utils.session.GlobalNotificationChange;
import jame.dev.utils.session.SessionManager;
import jame.dev.utils.ui.CustomAlert;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log
public class ShowCopies {
   @FXML
   private TextField txtFilter;
   @FXML
   private TableView<CopyDetailsDto> tableCopies;
   @FXML
   private TableColumn<CopyDetailsDto, Integer> colCopyN;
   @FXML
   private TableColumn<CopyDetailsDto, String> colTitle;
   @FXML
   private TableColumn<CopyDetailsDto, EGenre> colGenre;
   @FXML
   private TableColumn<CopyDetailsDto, EStatusCopy> colStatus;
   @FXML
   private TableColumn<CopyDetailsDto, ELanguage> colLanguage;
   @FXML
   private TextField txtIdCopy;
   @FXML
   private TextField txtDateNow;
   @FXML
   private TextField txtDays;
   @FXML
   private Button btnLoan;
   @FXML
   private Button btnClear;

   private static final CRUDRepo<LoanEntity> LOANS_REPO = new LoanService();
   private static List<CopyDetailsDto> copies;
   private static final Joinable<CopyDetailsDto> COPY_INFO = new CopyDetailsService();
   private static final CRUDRepo<CopyEntity> REPO_COPIES = new CopyService();
   private FilteredList<CopyDetailsDto> filteredList;
   private static final CustomAlert ALERT = CustomAlert.getInstance();
   private static final GlobalNotificationChange changes = GlobalNotificationChange.getInstance();
   private int indexSelected;

   @FXML
   private void initialize() throws IOException {
      copies = COPY_INFO.getJoins();
      tableConfig();
      this.btnLoan.setOnAction(this::handleLoan);
      this.btnClear.setOnAction(this::handleClear);
      this.txtFilter.setOnKeyTyped(keyEvent -> this.handleFilter(keyEvent, filteredList));
   }

   @FXML
   private void tableConfig() {
      this.colCopyN.setCellValueFactory(data ->
              new SimpleIntegerProperty(data.getValue().copyNum()).asObject());
      this.colTitle.setCellValueFactory(data ->
              new SimpleStringProperty(data.getValue().bookName()));
      this.colGenre.setCellValueFactory(data ->
              new SimpleObjectProperty<>(data.getValue().genre()));
      this.colStatus.setCellValueFactory(data ->
              new SimpleObjectProperty<>(data.getValue().status()));
      this.colLanguage.setCellValueFactory(data ->
              new SimpleObjectProperty<>(data.getValue().language()));

      this.tableCopies.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      ObservableList<CopyDetailsDto> observableList = FXCollections.observableArrayList(copies);
      this.tableCopies.setItems(observableList);

      this.tableCopies.setOnMouseClicked(_ ->
              Optional.ofNullable(this.tableCopies.getSelectionModel().getSelectedItem())
                      .ifPresent(copy -> {
                         txtIdCopy.setText(String.valueOf(copy.idCopy()));
                         txtDateNow.setText(LocalDate.now().toString());
                         this.btnLoan.setDisable(false);
                         this.indexSelected = this.tableCopies.getSelectionModel().getSelectedIndex();
                      })
      );
      filteredList = new FilteredList<>(this.tableCopies.getItems(), _ -> true);
   }

   @FXML
   private void handleLoan(ActionEvent event) {
      try {
         int plusDays = Integer.parseInt(txtDays.getText().trim());
         if (plusDays > 60) {
            ALERT.buildAlert(Alert.AlertType.WARNING, "NOT ALLOWED", "You can't request a loan for more than 60 days")
                    .show();
            return;
         }
         this.save(plusDays);
         ALERT.buildAlert(Alert.AlertType.INFORMATION, "SUCCESS", "Loan Saved")
                 .show();
         changes.registerChange(EGlobalNames.LOAN_CLIENT.name());
         changes.registerChange(EGlobalNames.HISTORY.name());

      } catch (NullPointerException e) {
         ALERT.buildAlert(Alert.AlertType.ERROR, "ERROR", "THE FIELDS CAN'T BE NULL")
                 .show();
         log.severe(e.getMessage());
      } finally {
         this.btnClear.fire();
      }
   }

   @FXML
   private void handleClear(ActionEvent event) {
      this.btnLoan.setDisable(true);
      this.txtIdCopy.clear();
      this.txtDateNow.clear();
      this.txtDays.clear();
      this.txtFilter.clear();
      this.tableCopies.getSelectionModel().clearSelection();
      this.indexSelected = -1;
   }

   @FXML
   private void handleFilter(KeyEvent event, FilteredList<CopyDetailsDto> filteredList) {
      String text = txtFilter.getText().trim();
      String textEnum = text.toUpperCase();
      filteredList.setPredicate(copy -> {
         if (text.isEmpty()) return true;
         try {
            return copy.idCopy().toString().contains(text) ||
                    String.valueOf(copy.copyNum()).contains(text) ||
                    copy.bookName().contains(text) ||
                    copy.genre().name().contains(textEnum) ||
                    copy.status().name().contains(textEnum) ||
                    copy.language().name().contains(textEnum);
         } catch (IllegalArgumentException e) {
            return false;
         }
      });
      this.tableCopies.setItems(filteredList);
   }

   private void save(int plusDays){
      LoanEntity loan = LoanEntity.builder()
              .uuid(UUID.randomUUID())
              .idUser(SessionManager.getInstance().getSessionDto().id())
              .idCopy(Integer.parseInt(txtIdCopy.getText().trim()))
              .loanDate(LocalDate.parse(txtDateNow.getText().trim()))
              .returnDate(LocalDate.now().plusDays(plusDays))
              .statusLoan(EStatusLoan.ON_LOAN)
              .build();
      if (CheckFinesUtil.isFined(loan.getIdUser())) {
         ALERT.buildAlert(Alert.AlertType.INFORMATION, "UNAUTHORIZED", "You have fines, you can't request loans now.")
                 .show();
         return;
      }
      LOANS_REPO.save(loan);
      REPO_COPIES.findByUuid(this.tableCopies.getSelectionModel().getSelectedItem().uuid())
              .ifPresent(copy -> {
                 copy.setBorrowed(true);
                 REPO_COPIES.update(copy);
              });
      this.tableCopies.getItems().remove(indexSelected);
      copies.remove(indexSelected);
   }
}
