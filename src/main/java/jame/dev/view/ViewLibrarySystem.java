package jame.dev.view;

import jame.dev.Main;
import jame.dev.models.entitys.UserEntity;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.schema.Schema;
import jame.dev.service.UserService;
import jame.dev.utils.ExecutorTabLoaderUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewLibrarySystem extends Application {


   private static final ExecutorService executorService = Executors.newFixedThreadPool(4);
   private final CRUDRepo<UserEntity> repoUsers = new UserService();

   @Override
   public void init() throws Exception {
      new Schema();
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
      executorService.shutdown();
      ExecutorTabLoaderUtil.shutDownExecutor();
      System.gc();
   }

}
