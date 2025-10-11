package jame.dev.controller.user.modal;

import jame.dev.dtos.books.BooksDto;
import jame.dev.models.entitys.CopyEntity;
import jame.dev.models.entitys.LoanEntity;
import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import jame.dev.models.enums.EStatusLoan;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.CopyService;
import jame.dev.service.LoanService;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller class for gives functionality to the view of ModalBookCopies.fxml
 */
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
   private TableColumn<CopyEntity, String> colBorrowed;
   @FXML
   private TableColumn<CopyEntity, EStatusCopy> colStatus;
   @FXML
   private TableColumn<CopyEntity, ELanguage> colLang;

   @FXML
   private Button btnLoan;
   @FXML
   private Button btnClear;

   private final CRUDRepo<CopyEntity> copyRepo = new CopyService();
   private final CRUDRepo<LoanEntity> loanRepo = new LoanService();

   private static List<CopyEntity> copies;
   private CopyEntity copySelected;
   private int idBook;
   private static final CustomAlert ALERT = CustomAlert.getInstance();
   private static final GlobalNotificationChange changes = GlobalNotificationChange.getInstance();

   /**
    * Initializes the component actions, like listeners, data etc.
    * @throws IOException
    */
   @FXML
   private void initialize() throws IOException {
      this.btnLoan.setOnAction(this::handleSaveLoan);
      this.btnClear.setOnAction(this::handleClear);
   }

   /**
    * Used for call the controller class and then calls the method out from the class.
    * it loads all the copies associated with the current book in the view and applies
    * the config defined on the method tableConfig().
    * @param booksDto the {@link BooksDto} object.
    */
   @FXML
   public void setBookInfo(BooksDto booksDto) {
      this.idBook = booksDto.id();
      this.labelBookName.setText("%s %s".formatted(labelBookName.getText(), booksDto
              .title()));
      this.textDateNow.setText(LocalDate.now().toString());
      //data
      Optional.ofNullable(this.copyRepo.getAll())
              .ifPresent(copyEntities ->
                      copies = copyEntities.stream()
                              .filter(copy ->
                                      (copy.getIdBook() == this.idBook && copy.getCopyNum() > 1) && (!copy.getBorrowed()))
                              .toList()
              );
      tableConfig();
   }

   /**
    * set the properties, listeners, data, selection for the table on this class and hi columns.
    */
   @FXML
   private void tableConfig() {
      //columns
      colUuid.setCellValueFactory(copy ->
              new SimpleObjectProperty<>(copy.getValue().getUuid()));
      colCopyNum.setCellValueFactory(copy ->
              new SimpleIntegerProperty(copy.getValue().getCopyNum()).asObject());
      colBorrowed.setCellValueFactory(data ->
              new SimpleStringProperty(data.getValue().getBorrowed() ? "YES" : "NO"));
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
                         this.btnLoan.setDisable(false);
                      })
      );
   }

   /**
    * Handles the request of a loan, it's going to evaluate if the requested days for the loan it's greater than
    * 60, if it is the return out of the method and notifies the user.
    * Also evaluates that the user doesn't have fines to do the loan.
    * @param event the ActionEvent.
    */
   @FXML
   private void handleSaveLoan(ActionEvent event) {
      Optional.ofNullable(this.copySelected)
              .ifPresentOrElse(copy -> {
                         int plusDays = Integer.parseInt(textDaysLoan.getText().trim());
                         if (plusDays > 60) {
                            ALERT.errorAlert("You can't request a loan for more than 60 days");
                            return;
                         }
                         LoanEntity loan = LoanEntity.builder()
                                 .uuid(UUID.randomUUID())
                                 .idUser(SessionManager.getInstance().getSessionDto().id())
                                 .idCopy(copy.getId())
                                 .loanDate(LocalDate.parse(textDateNow.getText().trim()))
                                 .returnDate(LocalDate.now().plusDays(plusDays))
                                 .statusLoan(EStatusLoan.ON_LOAN)
                                 .build();
                         if (CheckFinesUtil.isFined(loan.getIdUser())) {
                            ALERT.errorAlert("You have fines, you can't request loans now.");
                            return;
                         }
                         this.save(loan, copy);
                         ALERT.infoAlert("Loan saved.");
                         changes.registerChange(EGlobalNames.LOAN_CLIENT.name());
                         changes.registerChange(EGlobalNames.HISTORY.name());
                      },
                      () -> ALERT.errorAlert("No value present."));
      this.btnClear.fire();
   }

   /**
    * Cleans up the fields in the view, enable - diable components and set variables aux.
    * @param event the ActionEvent
    */
   @FXML
   private void handleClear(ActionEvent event) {
      this.tableCopies.getSelectionModel().clearSelection();
      this.textUuid.clear();
      this.textDateNow.clear();
      this.textDaysLoan.clear();
      this.copySelected = null;
      this.btnLoan.setDisable(true);
   }

   /**
    * Logic for add a Loan.
    * Takes a {@link LoanEntity} object for save it on the db and a
    * {@link CopyEntity} object for set his property borrowed to true, so being it this copy can't
    * be loaned again until be returned.
    * @param loan {@link LoanEntity} object.
    * @param copy {@link CopyEntity} object.
    */
   private void save(LoanEntity loan, CopyEntity copy){
      this.loanRepo.save(loan);
      copy.setBorrowed(true);
      this.copyRepo.update(copy);
   }
}
