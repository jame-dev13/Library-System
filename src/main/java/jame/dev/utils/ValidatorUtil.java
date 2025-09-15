package jame.dev.utils;

public final class ValidatorUtil {

   public static boolean isValidString(String... s){
      boolean matches = false;
      boolean isBlank = false;
      for (String string : s) {
         matches = string.matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ@., ]+$");
         isBlank = string.isBlank();
      }

      return !isBlank && matches;
   }
}
