package jame.dev.utils;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.function.Function;

public class ComponentValidationUtil {

   public static void addValidation(TextField text, Label label, Function<String, Boolean> validator, String msg){
      text.textProperty().addListener(
              (_, _, newText) -> {
                 if(!validator.apply(newText)){
                    label.setText(msg);
                    label.setVisible(true);
                    return;
                 }
                 label.setVisible(false);
              });
   }
}
