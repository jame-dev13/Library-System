package jame.dev.view;

import jame.dev.Main;
import jame.dev.connection.ConnectionDB;
import jame.dev.schema.Schema;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewLibrarySystem extends Application {

    @Override
    public void init() throws Exception {
        Thread db = new Thread(Schema::new);
        db.start();
        try{
            db.join();
        }catch(RuntimeException e){
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader =
                new FXMLLoader(Main.class.getResource("/templates/login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        ConnectionDB.getInstance().close();
        System.gc();
    }
}
