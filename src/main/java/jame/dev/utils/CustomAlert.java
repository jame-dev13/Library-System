package jame.dev.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class CustomAlert {
    private static CustomAlert instance;
    private CustomAlert(){}

    public static CustomAlert getInstance(){
        if(instance == null){
            instance = new CustomAlert();
        }
        return instance;
    }

    public Alert buildAlert(AlertType type, String header, String context){
        Alert al = new Alert(type);
        al.setTitle("ALERT!");
        al.setHeaderText(header);
        al.setContentText(context);
        return al;
    }
}
