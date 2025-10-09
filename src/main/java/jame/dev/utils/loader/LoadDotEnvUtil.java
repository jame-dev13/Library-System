package jame.dev.utils.loader;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;

public final class LoadDotEnvUtil {
   private static volatile LoadDotEnvUtil loader;
   private static final Dotenv dotenv = Dotenv.load();
   private LoadDotEnvUtil() {
   }

   public synchronized static LoadDotEnvUtil getInstance() {
      if (loader == null) {
         loader = new LoadDotEnvUtil();
      }
      return loader;
   }

   public Map<String, String> getMapDB() {
      return Map.of(
              "URL", dotenv.get("DB_URL"),
              "USER", dotenv.get("DB_USER"),
              "PWD", dotenv.get("DB_PWD"));
   }
}
