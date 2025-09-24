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
           Executors.newFixedThreadPool(8);

   public static void loadTab (String fxmlPath, Tab tab){
      if(tab.getContent() != null) return;
      executorService.submit(() -> {
         try{
            URL url = Main.class.getResource(fxmlPath);
            Optional.ofNullable(url).orElseThrow();
            Parent root = FXMLLoader.load(url);
            Platform.runLater(() -> tab.setContent(root));
         } catch (IOException e) {
            log.severe("Error loading the resource: " + e.getLocalizedMessage());
            Platform.runLater(() -> tab.setContent(new Label("Error loading UI")));
         }
      });
   }

   public static void shutDownExecutor(){
      executorService.shutdown();
   }
}
