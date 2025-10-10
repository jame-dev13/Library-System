package jame.dev.controller.admin;

import jame.dev.models.entitys.BookEntity;
import jame.dev.models.entitys.CopyEntity;
import jame.dev.models.enums.ELanguage;
import jame.dev.models.enums.EStatusCopy;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.service.CopyService;
import jame.dev.utils.ui.CustomAlert;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author jame-dev13
 */
@Log
public class Copies {

   @FXML
   private TextField txtCopyNum;
   @FXML
   private TableView<CopyEntity> tableCopies;
   @FXML
   private TableColumn<CopyEntity, UUID> colUuid;
   @FXML
   private TableColumn<CopyEntity, Integer> colIdBook;
   @FXML
   private TableColumn<CopyEntity, Integer> colCopyNum;
   @FXML
   private TableColumn<CopyEntity, String> colBorrowed;
   @FXML
   private TableColumn<CopyEntity, EStatusCopy> colStatus;
   @FXML
   private TableColumn<CopyEntity, ELanguage> colLanguage;
   @FXML
   private TextField txtIdBook;
   @FXML
   private ComboBox<EStatusCopy> boxStatus;
   @FXML
   private ComboBox<ELanguage> boxLanguage;
   @FXML
   private Button btnClear;
   @FXML
   private Button btnSave;
   @FXML
   private Button btnUpdate;
   @FXML
   private Button btnDelete;

   private CRUDRepo<CopyEntity> repo;
   private static List<CopyEntity> copies;
   private UUID uuidSelected;
   private int indexSelected;
   private static final CustomAlert alert = CustomAlert.getInstance();

   @FXML
   private void initialize() throws IOException {
      this.repo = new CopyService();
      //fields
      this.boxStatus.setItems(FXCollections.observableArrayList(EStatusCopy.values()));
      this.boxLanguage.setItems(FXCollections.observableArrayList(ELanguage.values()));
      //buttons
      this.btnClear.setOnAction(this::handleClear);
      this.btnSave.setOnAction(this::handleSave);
      this.btnUpdate.setOnAction(this::handleUpdate);
      this.btnDelete.setOnAction(this::handleDelete);
   }

   @FXML
   private void configTable() {
      //columns
      this.colUuid.setCellValueFactory(data ->
              new SimpleObjectProperty<>(data.getValue().getUuid()));
      this.colIdBook.setCellValueFactory(data ->
              new SimpleIntegerProperty(data.getValue().getIdBook()).asObject());
      this.colCopyNum.setCellValueFactory(data ->
              new SimpleIntegerProperty(data.getValue().getCopyNum()).asObject());
      this.colBorrowed.setCellValueFactory(data ->
              new SimpleStringProperty(data.getValue().getBorrowed() ? "YES" : "NO"));
      this.colStatus.setCellValueFactory(data ->
              new SimpleObjectProperty<>(data.getValue().getStatusCopy()));
      this.colLanguage.setCellValueFactory(data ->
              new SimpleObjectProperty<>(data.getValue().getLanguage()));

      //selection
      this.tableCopies.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

      //data
      ObservableList<CopyEntity> observableList =
              FXCollections.observableArrayList(copies);
      this.tableCopies.setItems(observableList);

      //listener
      this.tableCopies.setOnMouseClicked(m -> {
         Optional.ofNullable(this.tableCopies.getSelectionModel().getSelectedItem())
                 .ifPresent(this::onSelection);
      });
   }

   @FXML
   public void setBookInfo(BookEntity book) {
      Optional.ofNullable(this.repo.getAll())
              .ifPresent(list -> copies = list
                      .stream()
                      .filter(copy -> copy.getIdBook() == book.getId())
                      .collect(Collectors.toList()));
      this.txtIdBook.setText(String.valueOf(book.getId()));

      txtCopyNum.setText(String.valueOf(copies.size() + 1));

      this.configTable();
   }

   @FXML
   private void handleClear(ActionEvent event) {
      this.txtCopyNum.setText(String.valueOf(copies.size() + 1));
      this.boxStatus.setValue(null);
      this.boxLanguage.setValue(null);
      this.tableCopies.getSelectionModel().clearSelection();
      this.btnSave.setDisable(false);
      this.btnUpdate.setDisable(true);
      this.btnDelete.setDisable(true);
      this.uuidSelected = null;
      this.indexSelected = -1;
   }

   @FXML
   private void handleSave(ActionEvent event) {
      try {
         this.save();
         alert.infoAlert("Copy saved.");
      } catch (NullPointerException e) {
         alert.errorAlert("Empty fields present.");
         log.severe(e.getMessage());
      } finally {
         this.btnClear.fire();
      }
   }

   @FXML
   private void handleUpdate(ActionEvent event) {
      this.repo.findByUuid(this.uuidSelected)
              .ifPresentOrElse(this::update,
                      () -> alert
                              .warningAlert("Not found."));
      this.btnClear.fire();
   }

   @FXML
   private void handleDelete(ActionEvent event) {
      alert.deleteConfirmAlert("Do you want to delete this record?")
              .ifPresent(confirmation -> {
                 if (confirmation == ButtonType.OK) {
                    this.repo.deleteByUuid(uuidSelected);
                    this.tableCopies.getItems().remove(indexSelected);
                    copies.remove(indexSelected);
                 }
              });
      this.btnClear.fire();
   }

   private void onSelection(CopyEntity selection) {
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
   }

   private void save() {
      CopyEntity copy = CopyEntity.builder()
              .uuid(UUID.randomUUID())
              .idBook(Integer.parseInt(txtIdBook.getText().trim()))
              .copyNum(Integer.parseInt(txtCopyNum.getText().trim()))
              .borrowed(false)
              .statusCopy(boxStatus.getSelectionModel().getSelectedItem())
              .language(boxLanguage.getSelectionModel().getSelectedItem())
              .build();
      this.repo.save(copy);
      this.tableCopies.getItems().add(copy);
      copies.add(copy);
   }

   private void update(CopyEntity copy) {
      copy.setStatusCopy(boxStatus.getSelectionModel().getSelectedItem());
      copy.setLanguage(boxLanguage.getSelectionModel().getSelectedItem());
      alert.updateConfirmAlert("Dou you want to update this record?")
              .ifPresent(confirmation -> {
                 if (confirmation == ButtonType.OK) {
                    this.repo.update(copy);
                    copies.set(indexSelected, copy);
                    this.tableCopies.getItems().set(indexSelected, copy);
                 }
              });
   }
}
