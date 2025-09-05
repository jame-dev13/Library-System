package jame.dev.controller.admin;

import jame.dev.models.entitys.BookEntity;
import jame.dev.models.entitys.CopyEntity;
import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.CopyService;
import jame.dev.utils.CustomAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author jame-dev13
 */
public class Copies {

   @FXML private TextField txtCopyNum;
   @FXML private TableView<CopyEntity> tableCopies;
   @FXML private TableColumn<CopyEntity, UUID> colUuid;
   @FXML private TableColumn<CopyEntity, Integer> colIdBook;
   @FXML private TableColumn<CopyEntity, Integer> colCopyNum;
   @FXML private TableColumn<CopyEntity, EStatusCopy> colStatus;
   @FXML private TableColumn<CopyEntity, ELanguage> colLanguage;
   @FXML private TextField txtIdBook;
   @FXML private ComboBox<EStatusCopy> boxStatus;
   @FXML private ComboBox<ELanguage> boxLanguage;
   @FXML private Button btnClear;
   @FXML private Button btnSave;
   @FXML private Button btnUpdate;
   @FXML private Button btnDelete;

   private CRUDRepo<CopyEntity> repo;
   private static List<CopyEntity> copies;
   private UUID uuidSelected;
   private int indexSelected;

   /**
    * Initializes components, global data and listeners, everything of that type
    * must be in this method.
    * @throws IOException an Exception if the loader fails.
    */
   @FXML private void initialize() throws IOException {
      this.repo = new CopyService();
      Optional.ofNullable(this.repo.getAll())
              .ifPresent(list -> copies = list);
      //fields
      this.boxStatus.setItems(FXCollections.observableArrayList(EStatusCopy.values()));
      this.boxLanguage.setItems(FXCollections.observableArrayList(ELanguage.values()));
      //table config
      configTable();
      //buttons
      this.btnClear.setOnAction(this::handleClear);
      this.btnSave.setOnAction(this::handleSave);
      this.btnUpdate.setOnAction(this::handleUpdate);
      this.btnDelete.setOnAction(this::handleDelete);
   }

   @FXML private void configTable(){
      //columns
      this.colUuid.setCellValueFactory(new PropertyValueFactory<>("uuid"));
      this.colIdBook.setCellValueFactory(new PropertyValueFactory<>("idBook"));
      this.colCopyNum.setCellValueFactory(new PropertyValueFactory<>("copyNum"));
      this.colStatus.setCellValueFactory(new PropertyValueFactory<>("statusCopy"));
      this.colLanguage.setCellValueFactory(new PropertyValueFactory<>("language"));

      //selection
      this.tableCopies.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

      //data
      ObservableList<CopyEntity> observableList =
              FXCollections.observableArrayList(copies);
      this.tableCopies.setItems(observableList);

      //listener
      this.tableCopies.setOnMouseClicked(m -> {
         Optional.ofNullable(this.tableCopies.getSelectionModel().getSelectedItem())
                 .ifPresent(selection -> {
                    //set aux
                    this.uuidSelected = selection.getUuid();
                    this.indexSelected = this.tableCopies.getSelectionModel().getSelectedIndex();
                    //set fields
                    txtIdBook.setText(String.valueOf(selection.getIdBook()));
                    txtCopyNum.setText(String.valueOf(selection.getCopyNum()));
                    boxStatus.setValue(selection.getStatusCopy());
                    boxLanguage.setValue(selection.getLanguage());
                    //enable buttons
                    this.btnUpdate.setDisable(false);
                    this.btnDelete.setDisable(false);
                    this.btnSave.setDisable(true);
                 });
      });
   }

   @FXML public void setIdBook(BookEntity book){
      this.txtIdBook.setText(String.valueOf(book.getId()));
      long count = copies.stream().filter(copy ->
                      copy.getIdBook() == Integer.parseInt(txtIdBook.getText()))
              .count();
      txtCopyNum.setText(String.valueOf(count + 1));
   }

   @FXML private void handleClear(ActionEvent event){
      this.txtIdBook.clear();
      this.txtCopyNum.clear();
      this.boxStatus.setValue(null);
      this.boxLanguage.setValue(null);

      this.tableCopies.getSelectionModel().clearSelection();

      if(!this.btnUpdate.isDisabled() && !this.btnDelete.isDisabled()){
         this.btnUpdate.setDisable(true);
         this.btnDelete.setDisable(true);
      }

      this.uuidSelected = null;
      this.indexSelected = -1;
   }

   @FXML private void handleSave(ActionEvent event){
      try{
         CopyEntity copy = CopyEntity.builder()
                 .uuid(UUID.randomUUID())
                 .idBook(Integer.parseInt(txtIdBook.getText().trim()))
                 .copyNum(Integer.parseInt(txtCopyNum.getText().trim()))
                 .statusCopy(boxStatus.getSelectionModel().getSelectedItem())
                 .language(boxLanguage.getSelectionModel().getSelectedItem())
                 .build();

         this.repo.save(copy);
         this.tableCopies.getItems().add(copy);
         copies.add(copy);
         CustomAlert.getInstance()
                 .buildAlert(Alert.AlertType.INFORMATION, "SAVE", "Copy added successfully.")
                 .show();
      }catch (NullPointerException e){
         CustomAlert.getInstance()
                 .buildAlert(Alert.AlertType.ERROR, "ERROR", "Empty fields present.")
                 .show();
         throw new RuntimeException("All fields must contain data." + e);
      }
      finally {
         this.btnClear.fire();
      }
   }

   @FXML private void handleUpdate(ActionEvent event){
      this.repo.findByUuid(this.uuidSelected)
           .ifPresentOrElse(copy -> {
              copy.setCopyNum(Integer.parseInt(txtCopyNum.getText().trim()));
              copy.setStatusCopy(boxStatus.getSelectionModel().getSelectedItem());
              copy.setLanguage(boxLanguage.getSelectionModel().getSelectedItem());
              CustomAlert.getInstance()
                      .buildAlert(Alert.AlertType.CONFIRMATION, "UPDATE", "Do you want to update this record?")
                      .showAndWait()
                      .ifPresent(confirmation -> {
                         if(confirmation == ButtonType.OK){
                            this.repo.update(copy);
                            copies.set(indexSelected, copy);
                            this.tableCopies.getItems().set(indexSelected, copy);
                         }
                      });
           }, () -> CustomAlert.getInstance()
                   .buildAlert(Alert.AlertType.WARNING, "WARNING", "Not found.")
                   .show());
      this.btnClear.fire();
   }

   @FXML private void handleDelete(ActionEvent event){
      CustomAlert.getInstance()
              .buildAlert(Alert.AlertType.CONFIRMATION, "DELETE", "Do you want to delete this copy?")
              .showAndWait()
              .ifPresent(confirmation -> {
                 if(confirmation == ButtonType.OK){
                    this.repo.deleteByUuid(uuidSelected);
                    this.tableCopies.getItems().remove(indexSelected);
                    copies.remove(indexSelected);
                 }
              });
      this.btnClear.fire();
   }
}
