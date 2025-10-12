package jame.dev.utils.loader;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;

/**
 * This class  provides methods to get values for an .env file.
 * Must define different methods if there is a various groups of data in your
 * .env file that you need in
 * separated logic.
 * <p><b>Note: </b>This is a Singleton class.</p>
 */
public final class LoadDotEnvUtil {
   private static volatile LoadDotEnvUtil loader;
   private static final Dotenv dotenv = Dotenv.load();
   private LoadDotEnvUtil() {
   }

   /**
    * Gets the unique and current instance along the program for the class.
    * @return the {@code LoadDotEnvUtil} instance.
    */
   public synchronized static LoadDotEnvUtil getInstance() {
      if (loader == null) {
         loader = new LoadDotEnvUtil();
      }
      return loader;
   }

   /**
    * Returns the values associated in the .env file with the database properties
    * in a Map witch is unmodifiable.
    * @return an unmodifiable Map.
    */
   public Map<String, String> getMapDB() {
      return Map.of(
              "URL", dotenv.get("DB_URL"),
              "USER", dotenv.get("DB_USER"),
              "PWD", dotenv.get("DB_PWD"));
   }

   /**
    * Returns the values associated in the .env file with the Email Sender service.
    * @return an unmodifiable Map.
    */
   public Map<String, String> getMapEmailSender(){
      return Map.of(
              "FROM", dotenv.get("MAIL_FROM"),
              "APP_PWD", dotenv.get(("PWD_APP")),
              "PORT", dotenv.get("PORT")
      );
   }
}
