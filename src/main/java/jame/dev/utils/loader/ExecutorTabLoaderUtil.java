package jame.dev.utils.loader;

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

/**
 * This class uses an {@code ExecutorService executorService =
 * Executors.newFixedThreadPool(n);} to perform tasks in parallel, witch in this case is
 * Load Tabs in a TabPane.
 * <p>Here is an Example:
 * <pre>
 *       {@code
 *       public class myViewController{
 *          private String[] routes = {"/templates/myView.fxml", "/templates/myViewTwo.fxml"};
 *          @FXML private Tab myTab;
 *          @FXML private Tab myTab2;
 *
 *          @FXML private void initialize() throws IOException {
 *             ExecutorTabLoaderUtil.loadTab(routes[0], myTab);
 *             ExecutorTabLoaderUtil.loadTab(routes[1], myTab2);
 *          }
 *       }
 *       }
 *    </pre>
 * <p/>
 */
@Log
public final class ExecutorTabLoaderUtil {
   private static final ExecutorService executorService =
           Executors.newFixedThreadPool(5);

   /**
    * Loads the given {@code fxmlPath} content into the given {@code tab}.
    * @param fxmlPath the fxmlPath to load.
    * @param tab the Tab to load the content.
    */
   public static void loadTab(String fxmlPath, Tab tab) {
      executorService.submit(() -> {
         try {
            URL url = Main.class.getResource(fxmlPath);
            Optional.ofNullable(url).orElseThrow();
            Parent root = FXMLLoader.load(url);
            Platform.runLater(() -> tab.setContent(root));
         } catch (IOException e) {
            log.severe("Loading tab went wrong: " + e.getMessage());
            Platform.runLater(() -> tab.setContent(new Label("Error loading UI")));
         }
      });
   }

   /**
    * This method must be called at the point the application is going to close.
    */
   public static void shutDownExecutor() {
      executorService.shutdown();
   }

   /**
    * Use this method if you need to reload the content in the given {@code Tab}.
    * @param path the fxmlPath to reload.
    * @param tab the Tab witch contains the reloaded content.
    */
   public static void reLoad(String path, Tab tab) {
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
