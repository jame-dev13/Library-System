package jame.dev.controller.user;

import jame.dev.Main;
import jame.dev.controller.user.modal.ModalBookCopies;
import jame.dev.dtos.BooksDto;
import jame.dev.models.enums.EGenre;
import jame.dev.service.BookService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Books {

   @FXML private ComboBox<EGenre> boxGenre;
   @FXML private Button btnClearFilter;
   @FXML private Label lblBookCount;
   @FXML private Button btnShowCopies;
   @FXML private TableView<BooksDto> tableBooks;
   @FXML private TableColumn<BooksDto, UUID> colUuid;
   @FXML private TableColumn<BooksDto, String> colTitle;
   @FXML private TableColumn<BooksDto, String> colAuthor;
   @FXML private TableColumn<BooksDto, String> colEditorial;
   @FXML private TableColumn<BooksDto, String> colIsbn;
   @FXML private TableColumn<BooksDto, LocalDate> colPubDate;
   @FXML private TableColumn<BooksDto, Integer> colPages;
   @FXML private TableColumn<BooksDto, EGenre> colGenre;

   private static final String MODAL_BOOK_COPIES = "/templates/userPanes/modal/modalBookCopies.fxml";
   private static final String MODAL_COPIES = "/templates/userPanes/modal/modalShowCopies.fxml";
   private static List<BooksDto> books;
   private static FilteredList<BooksDto> filteredList;

   @FXML private void initialize() throws IOException {
      this.loadDto();
      configTable();
      this.boxGenre.setItems(FXCollections.observableArrayList(EGenre.values()));
      this.lblBookCount.setText(books.size() + " different books.");
      this.btnShowCopies.setOnAction(_ -> this.loadModal(MODAL_COPIES));
      this.btnClearFilter.setOnAction(_ -> this.boxGenre.getSelectionModel().clearSelection());
      //listener combobox
      filteredList = new FilteredList<>(this.tableBooks.getItems(), p -> true);
      this.tableBooks.setItems(filteredList);
      this.boxGenre.valueProperty().addListener((obs, oldV, newV)-> {
         filteredList.setPredicate(book -> {
            if(newV == null) {
               this.btnClearFilter.fire();
               return true;
            }
            return book.genre() == newV;
         });
      });
   }

   @FXML private void configTable(){
      //columns
      colUuid.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().uuid()));
      colTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().title()));
      colAuthor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().author()));
      colEditorial.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().editorial()));
      colIsbn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().ISBN()));
      colPubDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().pubDate()));
      colPages.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().numPages()).asObject());
      colGenre.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().genre()));

      //selection
      this.tableBooks.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

      //data
      ObservableList<BooksDto> observableList = FXCollections.observableArrayList(books);
      this.tableBooks.setItems(observableList);

      //listener
      this.tableBooks.setOnMouseClicked(mouse -> {
         int clicks = mouse.getClickCount();
         if(clicks == 2) {
            BooksDto dto  = this.tableBooks.getSelectionModel().getSelectedItem();
            this.loadModal(dto);
         }
         this.tableBooks.getSelectionModel().clearSelection();
      });
   }

   private void loadDto(){
      books = new BookService().getAll().stream()
              .map(book -> BooksDto.builder()
                      .id(book.getId())
                      .uuid(book.getUuid())
                      .title(book.getTitle())
                      .author(book.getAuthor())
                      .editorial(book.getEditorial())
                      .ISBN(book.getISBN())
                      .pubDate(book.getPubDate())
                      .numPages(book.getNumPages())
                      .genre(book.getGenre())
                      .build())
              .toList();
   }

   @FXML private void loadModal(String route){
      try{
         URL url = Main.class.getResource(route);
         FXMLLoader loader = new FXMLLoader(url);
         Parent root = loader.load();

         Stage stage = new Stage();
         stage.setTitle("Copies");
         stage.setScene(new Scene(root));
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.showAndWait();
      }catch (IOException e){
         throw new RuntimeException("Can´t load the resource view: ", e);
      }
   }

   @FXML private void loadModal(BooksDto booksDto){
      try{
         URL url = Main.class.getResource(Books.MODAL_BOOK_COPIES);
         FXMLLoader loader = new FXMLLoader(url);

         Parent root = loader.load();

         ModalBookCopies controller = loader.getController();
         controller.setBookInfo(booksDto);

         Stage stage = new Stage();
         stage.setTitle("Copies");
         stage.setScene(new Scene(root));
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.showAndWait();
      }catch (IOException e){
         throw new RuntimeException("Can´t load the resource view: ", e);
      }
   }
}
