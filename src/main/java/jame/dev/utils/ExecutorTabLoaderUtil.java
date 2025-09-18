package jame.dev.utils;

import jame.dev.Main;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Log
public final class ExecutorTabLoaderUtil {
   private static final ExecutorService executorService =
           Executors.newFixedThreadPool(4);

   public static void loadTab (String fxmlPath, Tab tab){
      if(tab.getContent() != null) return;
      executorService.submit(() -> {
         try{
            Optional.ofNullable(FXMLLoader.load(Objects
                            .requireNonNull(Main.class.getResource(fxmlPath))))
                    .ifPresent(root -> Platform.runLater(() -> tab.setContent((Node) root)));
         } catch (IOException e) {
            log.severe("Error loading the resource: " + e.getLocalizedMessage());
            e.printStackTrace();
            Platform.runLater(() -> tab.setContent(new Label("Error loading UI")));
         }
      });
   }

   public static void shutDownExecutor(){
      executorService.shutdown();
   }
}
