package jame.dev.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Singleton class for communicates things to the client.
 */
public final class CustomAlert {
    private static CustomAlert instance;
    private CustomAlert(){}

   /**
    * creates the Instance and maintains it.
    * @return the instance of this class.
    */
    public synchronized static CustomAlert getInstance(){
        if(instance == null){
            instance = new CustomAlert();
        }
        return instance;
    }

   /**
    * Builds an Alert.
    * @param type the {@link AlertType}
    * @param header the Header text for the alert
    * @param context the context fo the alert
    * @return an {@link Alert}
    */
    public Alert buildAlert(AlertType type, String header, String context){
        Alert al = new Alert(type);
        al.setTitle("ALERT!");
        al.setHeaderText(header);
        al.setContentText(context);
        return al;
    }
}
