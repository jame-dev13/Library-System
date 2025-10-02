package jame.dev.utils;

import jame.dev.Main;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Log
public final class ExecutorTabLoaderUtil {
   private static final ExecutorService executorService =
           Executors.newFixedThreadPool(5);

   public static void loadTab (String fxmlPath, Tab tab){
      executorService.submit(() -> {
         try{
            URL url = Main.class.getResource(fxmlPath);
            Optional.ofNullable(url).orElseThrow();
            Parent root = FXMLLoader.load(url);
            Platform.runLater(() -> tab.setContent(root));
         } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> tab.setContent(new Label("Error loading UI")));
         }
      });
   }

   public static void shutDownExecutor(){
      executorService.shutdown();
   }
   public static void reLoad(String path, Tab tab){
      URL url = Main.class.getResource(path);
      Optional.ofNullable(url).orElseThrow();
      try {
         Parent root = FXMLLoader.load(url);
         tab.setContent(root);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
