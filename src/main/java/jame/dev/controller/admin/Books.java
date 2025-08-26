package jame.dev.controller.admin;

import jame.dev.models.entitys.BookEntity;
import jame.dev.models.enums.ELanguage;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Date;
import java.util.UUID;

public class Books {

    //Fields
    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtEditorial;
    @FXML private TextField txtPubDate;
    @FXML private TextField txtPages;
    @FXML private TextField txtGenre;
    @FXML private ComboBox boxLanguages;
    @FXML private TextField txtSearch;

    //buttons
    @FXML private Button btnClear;
    @FXML private Button btnSave;
    @FXML private Button btnUpdate;
    @FXML private Button btnDrop;

    //Toggle Buttons
    @FXML private ToggleButton togId;
    @FXML private ToggleButton togTitle;
    @FXML private ToggleButton togIsbn;
    @FXML private ToggleButton togDate;
    //Table
    @FXML private TableView<BookEntity> tableBooks;
    @FXML private TableColumn<BookEntity, String> colAuthor;
    @FXML private TableColumn<BookEntity, String> colEditorial;
    @FXML private TableColumn<BookEntity, String> colIsbn;
    @FXML private TableColumn<BookEntity, Date> colPubDate;
    @FXML private TableColumn<BookEntity, Integer> colPages;
    @FXML private TableColumn<BookEntity, String> colGenre;
    @FXML private TableColumn<BookEntity, ELanguage> colLang;
    @FXML private TableColumn<BookEntity, UUID> colUuid;
    @FXML private TableColumn<BookEntity, String> colTitle;

    @FXML private void initialize(){

    }

}
