package jame.dev.utils.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Singleton class for communicates things to the client.
 */
public final class CustomAlert {
   private static volatile CustomAlert instance;
   private Alert alert;

   private CustomAlert() {
   }

   /**
    * creates the Instance and maintains it.
    *
    * @return the instance of this class.
    */
   public synchronized static CustomAlert getInstance() {
      if (instance == null) {
         instance = new CustomAlert();
      }
      return instance;
   }

   /**
    * Builds an Alert.
    *
    * @param type    the {@link AlertType}
    * @param header  the Header text for the alert
    * @param context the context fo the alert
    * @return an {@link Alert}
    */
   public Alert buildAlert(AlertType type, String header, String context) {
      Alert al = new Alert(type);
      al.setTitle("ALERT!");
      al.setHeaderText(header);
      al.setContentText(context);
      return al;
   }

   private void setAlert() {
      if (alert == null) {
         alert = new Alert(AlertType.NONE);
         alert.setTitle("|--- Library System ---|");
      }
   }

   public void infoAlert(String msg) {
      setAlert();
      alert.setAlertType(AlertType.INFORMATION);
      alert.setHeaderText("INFO");
      alert.setContentText(msg);
      alert.show();
   }

   public Optional<ButtonType> updateConfirmAlert(String msg) {
      setAlert();
      alert.setAlertType(AlertType.CONFIRMATION);
      alert.setHeaderText("UPDATE");
      alert.setContentText(msg);
      return alert.showAndWait();

   }

   public Optional<ButtonType> deleteConfirmAlert(String msg) {
      setAlert();
      alert.setAlertType(AlertType.CONFIRMATION);
      alert.setHeaderText("DELETE");
      alert.setContentText(msg);
      return alert.showAndWait();
   }

   public void warningAlert(String msg) {
      setAlert();
      alert.setAlertType(AlertType.WARNING);
      alert.setHeaderText("WARNING");
      alert.setContentText(msg);
      alert.show();
   }

   public void errorAlert(String msg) {
      setAlert();
      alert.setAlertType(AlertType.ERROR);
      alert.setHeaderText("ERROR");
      alert.setContentText(msg);
      alert.show();
   }
}
