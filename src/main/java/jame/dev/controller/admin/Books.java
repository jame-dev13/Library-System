package jame.dev.controller.admin;

import jame.dev.models.entitys.BookEntity;
import jame.dev.models.enums.ELanguage;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.BookService;
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
   private TextField txtGenre;
   @FXML
   private ComboBox<ELanguage> boxLanguages;
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

   //Toggle Buttons
   @FXML
   private ToggleButton togId;
   @FXML
   private ToggleButton togTitle;
   @FXML
   private ToggleButton togIsbn;
   @FXML
   private ToggleButton togDate;
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
   private TableColumn<BookEntity, String> colGenre;
   @FXML
   private TableColumn<BookEntity, ELanguage> colLang;
   @FXML
   private TableColumn<BookEntity, UUID> colUuid;
   @FXML
   private TableColumn<BookEntity, String> colTitle;

   private CRUDRepo<BookEntity> repo;
   private UUID selectedUuid;
   private int selectedIndex;
   private static List<BookEntity> booksE;

   @FXML
   private void initialize() {
      //Service
      this.repo = new BookService();
      //Data
      booksE = this.repo.getAll();
      this.tableConfig();
      this.boxLanguages.setItems(FXCollections.observableArrayList(ELanguage.values()));
      //button actions
      this.btnClear.setOnAction(this::handleClear);
      this.btnSave.setOnAction(this::handleSave);
      this.btnUpdate.setOnAction(this::handleUpdate);
      this.btnDrop.setOnAction(this::handleDelete);
      //filter actions
      FilteredList<BookEntity> filteredData = new FilteredList<>(this.tableBooks.getItems(), p -> true);
      this.txtSearch.setOnKeyTyped(k -> this.handleTextChange(k, filteredData));
   }

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
      colLang.setCellValueFactory(new PropertyValueFactory<>("language"));
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
                    BookEntity book = this.tableBooks.getSelectionModel().getSelectedItem();
                    //set fields with the object 'book' values.
                    txtTitle.setText(book.getTitle());
                    txtAuthor.setText(book.getAuthor());
                    txtIsbn.setText(book.getISBN());
                    txtEditorial.setText(book.getEditorial());
                    pickerPubDate.setValue(book.getPubDate());
                    txtPages.setText(String.valueOf(book.getNumPages()));
                    txtGenre.setText(book.getGenre());
                    boxLanguages.setValue(book.getLanguage());
                 });
         this.btnUpdate.setDisable(false);
         this.btnDrop.setDisable(false);
      });
   }

   @FXML
   private void handleSave(ActionEvent e) {
      try {
         this.repo.save(
                 BookEntity.builder()
                         .uuid(UUID.randomUUID())
                         .title(txtTitle.getText().trim())
                         .author(txtAuthor.getText().trim())
                         .editorial(txtEditorial.getText().trim())
                         .ISBN(txtIsbn.getText().trim())
                         .pubDate(pickerPubDate.getValue())
                         .numPages(Integer.parseInt(txtPages.getText().trim()))
                         .genre(txtGenre.getText().trim())
                         .language(boxLanguages.getSelectionModel().getSelectedItem())
                         .build());
         CustomAlert.getInstance()
                 .buildAlert(Alert.AlertType.INFORMATION,
                         "SUCCESS",
                         "Book added!");
      } catch (NullPointerException ne) {
         CustomAlert.getInstance()
                 .buildAlert(Alert.AlertType.ERROR,
                         "ERROR",
                         "Can't have empty fields.")
                 .showAndWait();
         throw new RuntimeException(ne);
      }
   }

   @FXML
   private void handleClear(ActionEvent e) {
      txtTitle.clear();
      txtAuthor.clear();
      txtIsbn.clear();
      txtEditorial.clear();
      txtPages.clear();
      txtGenre.clear();
      txtSearch.clear();
      pickerPubDate.setValue(null);
      boxLanguages.getSelectionModel().clearSelection();
      this.tableBooks.getSelectionModel().clearSelection();
      if (!this.btnDrop.isDisabled() || !this.btnUpdate.isDisabled()) {
         this.btnUpdate.setDisable(true);
         this.btnDrop.setDisable(true);
      }
      this.selectedUuid = null;
      this.selectedIndex = -1;
   }

   @FXML
   private void handleUpdate(ActionEvent e) {
      Optional.ofNullable(selectedUuid)
              .ifPresent(uuid -> {
                 BookEntity book = this.repo.findByUuid(uuid).orElse(null);
                 if (book != null) {
                    book.setTitle(txtTitle.getText().trim());
                    book.setAuthor(txtAuthor.getText().trim());
                    book.setEditorial(txtEditorial.getText().trim());
                    book.setISBN(txtIsbn.getText().trim());
                    book.setPubDate(pickerPubDate.getValue());
                    book.setNumPages(Integer.parseInt(txtPages.getText().trim()));
                    book.setGenre(txtGenre.getText().trim());
                    book.setLanguage(boxLanguages.getSelectionModel().getSelectedItem());
                    this.repo.update(book);
                    booksE.set(selectedIndex, book);
                    this.tableBooks.getItems().set(selectedIndex, book);
                    CustomAlert.getInstance()
                            .buildAlert(Alert.AlertType.INFORMATION,
                                    "UPDATED",
                                    String.format("Record with identifier [%s] Updated!", book.getUuid()))
                            .showAndWait();
                    this.btnClear.fire();
                 } else {
                    CustomAlert.getInstance()
                            .buildAlert(Alert.AlertType.ERROR,
                                    "ERROR",
                                    "Not Found!")
                            .showAndWait();
                 }
              });
   }

   @FXML
   private void handleDelete(ActionEvent e) {
      Optional.ofNullable(selectedUuid).ifPresent(uuid -> {
         CustomAlert.getInstance()
                 .buildAlert(
                         Alert.AlertType.CONFIRMATION,
                         "DELETE",
                         "¿Do you want to delete this book record?"
                 )
                 .showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                       repo.deleteByUuid(uuid);
                       this.tableBooks.getItems().remove(selectedIndex);
                       booksE.remove(selectedIndex);
                       this.tableBooks.refresh();
                    } else return;
                 });
      });
   }

   @FXML
   private void handleTextChange(KeyEvent e, FilteredList<BookEntity> filteredData){
      String text = txtSearch.getText().trim();
      filteredData.setPredicate(book -> {
            if(text.isEmpty()) return true;
            try{
               return (book.getUuid().toString().contains(text)) ||
                       (book.getTitle().contains(text)) ||
                       (book.getAuthor().contains(text)) ||
                       (book.getISBN().contains(text)) ||
                       (book.getPubDate().toString().contains(text)) ||
                       (String.valueOf(book.getNumPages()).contains(text))||
                       (book.getGenre().contains(text)) ||
                       (book.getLanguage() == ELanguage.valueOf(text.toUpperCase()));
            }catch (IllegalArgumentException ex){
               return false;
            }
      });
      this.tableBooks.setItems(filteredData);
   }
}
