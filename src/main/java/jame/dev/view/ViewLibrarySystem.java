package jame.dev.view;

import jame.dev.Main;
import jame.dev.schema.Schema;
import jame.dev.utils.loader.ExecutorTabLoaderUtil;
import jame.dev.utils.loader.InitAdminUtil;
import jame.dev.utils.tools.TokenGenerator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Initializes the JavaFX Thread Application.
 */
public class ViewLibrarySystem extends Application {

   /**
    * This going to execute before the view's load.
    * In this case it will build the DB Shema and init an admin for uses the
    * application if there are no schema or no admin equal to this one.
    * @throws Exception
    */
   @Override
   public void init() throws Exception {
      new Schema();
      InitAdminUtil.init();
      System.out.println(TokenGenerator.genToken());
      System.out.println(TokenGenerator.genToken());
      System.out.println(TokenGenerator.genToken());
   }

   /**
    * This loads the view.
    * In this case it will load the Scene of a login page.
    * @param stage the Stage
    * @throws Exception
    */
   @Override
   public void start(Stage stage) throws Exception {
      FXMLLoader loader =
              new FXMLLoader(Main.class.getResource("/templates/login.fxml"));
      Scene scene = new Scene(loader.load());
      stage.setScene(scene);
      stage.setTitle("Library System");
      stage.show();
   }

   /**
    * This is called in the time the application is finished or closed.
    * It's going to shutting down an Executor of Threads.
    * @throws Exception
    */
   @Override
   public void stop() throws Exception {
      ExecutorTabLoaderUtil.shutDownExecutor();
      System.gc();
   }

}
