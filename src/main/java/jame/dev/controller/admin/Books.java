package jame.dev.controller.admin;

import jame.dev.Main;
import jame.dev.models.entitys.BookEntity;
import jame.dev.models.enums.EGenre;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.BookService;
import jame.dev.utils.CustomAlert;
import jame.dev.utils.GlobalNotificationChange;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


/**
 * Controller class for a specific pane that manages CRUD operations
 * for books.
 */
public class Books {
   //Fields
   @FXML
   private TextField txtTitle;
   @FXML
   private TextField txtAuthor;
   @FXML
   private TextField txtIsbn;
   @FXML
   private TextField txtEditorial;

   @FXML
   private DatePicker pickerPubDate;
   @FXML
   private TextField txtPages;
   @FXML
   private ComboBox<EGenre> boxGenre;
   @FXML
   private TextField txtSearch;

   //buttons
   @FXML
   private Button btnClear;
   @FXML
   private Button btnSave;
   @FXML
   private Button btnUpdate;
   @FXML
   private Button btnDrop;

   //Table
   @FXML
   private TableView<BookEntity> tableBooks;
   @FXML
   private TableColumn<BookEntity, String> colAuthor;
   @FXML
   private TableColumn<BookEntity, String> colEditorial;
   @FXML
   private TableColumn<BookEntity, String> colIsbn;
   @FXML
   private TableColumn<BookEntity, LocalDate> colPubDate;
   @FXML
   private TableColumn<BookEntity, Integer> colPages;
   @FXML
   private TableColumn<BookEntity, EGenre> colGenre;
   @FXML
   private TableColumn<BookEntity, UUID> colUuid;
   @FXML
   private TableColumn<BookEntity, String> colTitle;

   @FXML
   private Tooltip toolTip;

   private CRUDRepo<BookEntity> repo;
   private UUID selectedUuid;
   private int selectedIndex;
   private static List<BookEntity> booksE;
   private static final Set<String> bookNameSet = new HashSet<>();
   private static final CustomAlert alert = CustomAlert.getInstance();
   private static final GlobalNotificationChange changes = GlobalNotificationChange.getInstance();
   @FXML
   private void initialize() throws IOException {
      //Service
      this.repo = new BookService();
      //Data
      booksE = this.repo.getAll();
      bookNameSet.addAll(booksE.stream().map(BookEntity::getTitle).toList());
      this.tableConfig();
      this.boxGenre.setItems(FXCollections.observableArrayList(EGenre.values()));
      //button actions
      this.btnClear.setOnAction(this::handleClear);
      this.btnSave.setOnAction(this::handleSave);
      this.btnUpdate.setOnAction(this::handleUpdate);
      this.btnDrop.setOnAction(this::handleDelete);
      //filter actions
      FilteredList<BookEntity> filteredData = new FilteredList<>(this.tableBooks.getItems(), p -> true);
      this.txtSearch.setOnKeyTyped(k -> this.handleTextChange(k, filteredData));
   }

   /**
    * Configuration for the table, column type, selection, data and listeners
    */
   @FXML
   private void tableConfig() {
      //columns
      colUuid.setCellValueFactory(new PropertyValueFactory<>("uuid"));
      colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
      colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
      colEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
      colIsbn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
      colPubDate.setCellValueFactory(new PropertyValueFactory<>("pubDate"));
      colPages.setCellValueFactory(new PropertyValueFactory<>("numPages"));
      colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
      //table
      this.tableBooks.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      ObservableList<BookEntity> observable = FXCollections.observableArrayList(booksE);
      this.tableBooks.setItems(observable);

      //listeners
      this.tableBooks.setOnMouseClicked(l -> {
         //HandleNullPointerException
         Optional.ofNullable(this.tableBooks.getSelectionModel().getSelectedItem())
                 .ifPresent(selection -> {
                    this.selectedUuid = selection.getUuid();
                    this.selectedIndex = this.tableBooks.getSelectionModel().getSelectedIndex();
                    //set fields with the object 'book' values.
                    txtTitle.setText(selection.getTitle());
                    txtAuthor.setText(selection.getAuthor());
                    txtIsbn.setText(selection.getISBN());
                    txtEditorial.setText(selection.getEditorial());
                    pickerPubDate.setValue(selection.getPubDate());
                    txtPages.setText(String.valueOf(selection.getNumPages()));
                    boxGenre.setValue(selection.getGenre());
                 });
         this.btnUpdate.setDisable(false);
         this.btnDrop.setDisable(false);

         if (l.getClickCount() == 2) {
            this.loadPopOver(this.tableBooks.getSelectionModel().getSelectedItem());
         }
      });
   }

   /**
    * Handles the cleaning for the different types of fields in the view, includes
    * selections, and aux variables.
    *
    * @param e the ActionEvent
    */
   @FXML
   private void handleClear(ActionEvent e) {
      //fields
      txtTitle.clear();
      txtAuthor.clear();
      txtIsbn.clear();
      txtEditorial.clear();
      txtPages.clear();
      boxGenre.getSelectionModel().clearSelection();
      txtSearch.clear();
      pickerPubDate.setValue(null);
      //selection
      this.tableBooks.getSelectionModel().clearSelection();
      //disable buttons
      this.btnUpdate.setDisable(!this.btnUpdate.isDisabled());
      this.btnDrop.setDisable(!this.btnDrop.isDisabled());
      //reset global
      this.selectedUuid = null;
      this.selectedIndex = -1;
   }

   /**
    * Do an insertion through the repository, it maps the data and Build an
    * {@link BookEntity}
    *
    * @param e The ActionEvent
    */
   @FXML
   private void handleSave(ActionEvent e) {
      try {
         BookEntity book = BookEntity.builder()
                 .uuid(UUID.randomUUID())
                 .title(txtTitle.getText().trim())
                 .author(txtAuthor.getText().trim())
                 .editorial(txtEditorial.getText().trim())
                 .ISBN(txtIsbn.getText().trim())
                 .pubDate(pickerPubDate.getValue())
                 .numPages(Integer.parseInt(txtPages.getText().trim()))
                 .genre(boxGenre.getSelectionModel().getSelectedItem())
                 .build();
         if (bookNameSet.add(book.getTitle())) {
            this.repo.save(book);
            booksE.add(book);
            this.tableBooks.getItems().add(book);
            alert.buildAlert(Alert.AlertType.INFORMATION,
                    "SUCCESS",
                    "Book added!").show();
            changes.registerChange(getClass().getName());
         } else {
            alert.buildAlert(Alert.AlertType.WARNING, "WARNING", "Can't duplicate books").show();
         }
      } catch (NullPointerException ne) {
         alert.buildAlert(Alert.AlertType.ERROR,
                         "ERROR",
                         "Can't have empty fields.")
                 .show();
         throw new RuntimeException(ne);
      } finally {
         this.btnClear.fire();
      }
   }

   /**
    * Handles the update of the {@link BookEntity} object if it is present.
    *
    * @param e The ActionEvent
    */
   @FXML
   private void handleUpdate(ActionEvent e) {
      Optional.ofNullable(selectedUuid)
              .ifPresent(uuid -> {
                 this.repo.findByUuid(uuid).ifPresentOrElse(book -> {
                            book.setTitle(txtTitle.getText().trim());
                            book.setAuthor(txtAuthor.getText().trim());
                            book.setEditorial(txtEditorial.getText().trim());
                            book.setISBN(txtIsbn.getText().trim());
                            book.setPubDate(pickerPubDate.getValue());
                            book.setNumPages(Integer.parseInt(txtPages.getText().trim()));
                            book.setGenre(boxGenre.getSelectionModel().getSelectedItem());
                            //save changes
                            this.repo.update(book);
                            booksE.set(selectedIndex, book);
                            this.tableBooks.getItems().set(selectedIndex, book);
                            //confirmation
                            alert.buildAlert(Alert.AlertType.INFORMATION,
                                            "UPDATED",
                                            String.format("Book: [%s] Updated!", book.getTitle()))
                                    .show();
                            changes.registerChange(getClass().getName());
                         },
                         () -> alert.buildAlert(Alert.AlertType.ERROR,
                                         "ERROR",
                                         "Not Found!")
                                 .show());
              });
      this.btnClear.fire();
   }

   /**
    * Handles the deletion of an object {@link BookEntity} if the
    * user wants to.
    *
    * @param e The ActionEvent
    */
   @FXML
   private void handleDelete(ActionEvent e) {
      Optional.ofNullable(selectedUuid).ifPresent(uuid -> {
         alert.buildAlert(
                         Alert.AlertType.CONFIRMATION,
                         "DELETE",
                         "¿Do you want to delete this book record?"
                 )
                 .showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                       repo.deleteByUuid(uuid);
                       this.tableBooks.getItems().remove(selectedIndex);
                       BookEntity bookEntity = booksE.remove(selectedIndex);
                       bookNameSet.remove(bookEntity.getTitle());
                       changes.registerChange(getClass().getName());
                    }
                 });
      });
      this.btnClear.fire();
   }

   /**
    * Handles the way to filter the table data using a {@link FilteredList}
    *
    * @param e            The KeyEvent
    * @param filteredData The Data List
    */
   @FXML
   private void handleTextChange(KeyEvent e, FilteredList<BookEntity> filteredData) {
      String text = txtSearch.getText().trim();
      filteredData.setPredicate(book -> {
         if (text.isEmpty()) return true;
         try {
            return (book.getUuid().toString().contains(text)) ||
                    (book.getTitle().contains(text)) ||
                    (book.getAuthor().contains(text)) ||
                    (book.getISBN().contains(text)) ||
                    (book.getPubDate().toString().contains(text)) ||
                    (String.valueOf(book.getNumPages()).contains(text)) ||
                    (book.getGenre() == EGenre.valueOf(text.toUpperCase()));
         } catch (IllegalArgumentException ex) {
            return false;
         }
      });
      this.tableBooks.setItems(filteredData);
   }

   @FXML
   private void loadPopOver(BookEntity book) {
      try {
         FXMLLoader loader = new FXMLLoader((Main.class.getResource("/templates/adminPanes/copies.fxml")));
         Parent root = loader.load();

         Copies controller = loader.getController();

         controller.setBookInfo(book);

         Stage stage = new Stage();
         stage.setTitle("Copies");
         stage.setScene(new Scene(root));
         stage.resizableProperty().set(false);
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.showAndWait();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
