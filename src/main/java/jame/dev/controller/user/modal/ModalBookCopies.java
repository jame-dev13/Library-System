package jame.dev.controller.user.modal;

import jame.dev.dtos.BooksDto;
import jame.dev.models.entitys.CopyEntity;
import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.CopyService;
import jame.dev.service.LoanService;
import jame.dev.utils.CustomAlert;
import jame.dev.utils.SessionManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ModalBookCopies {

   @FXML
   private TextField textDateNow;
   @FXML
   private TextField textDaysLoan;
   @FXML
   private Label labelBookName;
   @FXML
   private TextField textUuid;

   @FXML
   private TableView<CopyEntity> tableCopies;
   @FXML
   private TableColumn<CopyEntity, UUID> colUuid;
   @FXML
   private TableColumn<CopyEntity, Integer> colCopyNum;
   @FXML
   private TableColumn<CopyEntity, EStatusCopy> colStatus;
   @FXML
   private TableColumn<CopyEntity, ELanguage> colLang;

   @FXML
   private Button btnLoan;
   @FXML
   private Button btnClear;

   private CRUDRepo<CopyEntity> copyRepo;
   private CRUDRepo<LoanEntity> loanRepo;

   private static List<CopyEntity> copies;
   private CopyEntity copySelected;
   private int idBook;

   @FXML
   private void initialize() throws IOException {
      this.copyRepo = new CopyService();
      this.loanRepo = new LoanService();
      this.btnLoan.setOnAction(this::handleSaveLoan);
      this.btnClear.setOnAction(this::handleClear);
   }

   @FXML
   public void setIdBook(BooksDto booksDto) {
      this.idBook = booksDto.id();
      this.labelBookName.setText("%s %s".formatted(labelBookName.getText(), booksDto
              .title()));
      this.textDateNow.setText(LocalDate.now().toString());
      //data
      Optional.ofNullable(this.copyRepo.getAll())
              .ifPresent(copyEntities -> {
                 copies = copyEntities.stream()
                         .filter(copy ->
                                 copy.getIdBook() == this.idBook && copy.getCopyNum() > 1)
                         .toList();
              });
      tableConfig();
   }

   @FXML
   private void tableConfig() {
      //columns
      colUuid.setCellValueFactory(copy ->
              new SimpleObjectProperty<>(copy.getValue().getUuid()));
      colCopyNum.setCellValueFactory(copy ->
              new SimpleIntegerProperty(copy.getValue().getCopyNum()).asObject());
      colStatus.setCellValueFactory(copy ->
              new SimpleObjectProperty<>(copy.getValue().getStatusCopy()));
      colLang.setCellValueFactory(copy ->
              new SimpleObjectProperty<>(copy.getValue().getLanguage()));
      //selection
      this.tableCopies.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      //data
      ObservableList<CopyEntity> observableList = FXCollections.observableArrayList(copies);
      this.tableCopies.setItems(observableList);
      //listener
      this.tableCopies.setOnMouseClicked(_ ->
              Optional.ofNullable(this.tableCopies.getSelectionModel().getSelectedItem())
                      .ifPresent(c -> {
                         this.copySelected = c;
                         this.textUuid.setText(copySelected.getUuid().toString());
                         this.textDateNow.setText(LocalDate.now().toString());
                      })
      );
   }

   @FXML
   private void handleSaveLoan(ActionEvent event) {
      Optional.ofNullable(this.copySelected)
              .ifPresentOrElse(copy -> {
                         this.loanRepo.save(
                                 LoanEntity.builder()
                                         .uuid(UUID.randomUUID())
                                         .idUser(SessionManager.getInstance().getSessionDto().id())
                                         .idCopy(copy.getId())
                                         .loanDate(LocalDate.parse(textDateNow.getText().trim()))
                                         .returnDate(LocalDate.now().plusDays(Integer.parseInt(textDaysLoan.getText().trim())))
                                         .statusLoan(EStatusLoan.ON_LOAN)
                                         .build()
                         );
                         CustomAlert.getInstance()
                                 .buildAlert(Alert.AlertType.INFORMATION, "SUCCESS", "Loan saved.")
                                 .show();
                      },
                      () -> CustomAlert.getInstance()
                              .buildAlert(Alert.AlertType.ERROR, "ERROR", "No value present.")
                              .show());
   }

   @FXML private void handleClear(ActionEvent event){
      this.tableCopies.getSelectionModel().clearSelection();
      this.textUuid.clear();
      this.textDateNow.clear();
      this.textDaysLoan.clear();
   }
}
