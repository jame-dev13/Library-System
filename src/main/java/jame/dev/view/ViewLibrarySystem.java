package jame.dev.view;

import jame.dev.Main;
import jame.dev.schema.Schema;
import jame.dev.utils.loader.ExecutorTabLoaderUtil;
import jame.dev.utils.loader.InitAdminUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewLibrarySystem extends Application {


   @Override
   public void init() throws Exception {
      new Schema();
      InitAdminUtil.init();
   }

   @Override
   public void start(Stage stage) throws Exception {
      FXMLLoader loader =
              new FXMLLoader(Main.class.getResource("/templates/login.fxml"));
      Scene scene = new Scene(loader.load());
      stage.setScene(scene);
      stage.setTitle("Library System");
      stage.show();
   }

   @Override
   public void stop() throws Exception {
      ExecutorTabLoaderUtil.shutDownExecutor();
      System.gc();
   }

}
