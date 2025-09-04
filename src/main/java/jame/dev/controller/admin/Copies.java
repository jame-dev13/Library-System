package jame.dev.controller.admin;

import jame.dev.models.entitys.BookEntity;
import jame.dev.models.entitys.CopyEntity;
import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.UUID;

public class Copies {

   @FXML private TextField txtCopyNum;
   @FXML private TableView<CopyEntity> tableCopies;
   @FXML private TableColumn<CopyEntity, UUID> colUuid;
   @FXML private TableColumn<CopyEntity, Integer> colIdBook;
   @FXML private TableColumn<CopyEntity, Integer> colCopyNum;
   @FXML private TableColumn<CopyEntity, EStatusCopy> colStatus;
   @FXML private TableColumn<CopyEntity, ELanguage> colLanguage;
   @FXML private TextField txtIdUser;
   @FXML private ComboBox<EStatusCopy> boxStatus;
   @FXML private ComboBox<ELanguage> boxLanguage;
   @FXML private Button btnClear;
   @FXML private Button btnSave;
   @FXML private Button btnUpdate;
   @FXML private Button btnDelete;

   @FXML private void initialize() throws IOException {

   }

   @FXML private void configTable(){

   }

   @FXML public void setIdBook(BookEntity book){
      txtIdUser.setText(String.valueOf(book.getId()));
   }
}
