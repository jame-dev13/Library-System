package jame.dev.utils;

import java.util.HashMap;
import java.util.Map;

public final class ValidatorUtil {

   public static boolean isValidString(String... s) {
      if (s == null || s.length == 0) return false;
      for (String string : s) {
         if (string == null || string.isBlank() || !string.matches("^[a-zA-záéíóúÁÉÍÓÚñÑ][a-zA-Z0-9áéíóúÁÉÍÓÚñÑ@'. ]+$")) {
            return false;
         }
      }
      return true;
   }

   public static boolean isEmailValid(String s) {
      return s.matches("^[a-zA-Z][a-zA-Z0-9.-_]+@+[a-zA-Z0-9.-]+\\.[a-zA-Z0-9]{2,}$");
   }

   public static boolean pwdIsStrong(String pwd) {
      Map<String, Integer> characterCount = new HashMap<>();
      characterCount.put("lower", 0);
      characterCount.put("upper", 0);
      characterCount.put("digit", 0);
      characterCount.put("spec", 0);
      for (char c : pwd.toCharArray()) {
         if (Character.isLowerCase(c)) {
            characterCount.put("lower", characterCount.get("lower") + 1);
         } else if (Character.isUpperCase(c)) {
            characterCount.put("upper", characterCount.get("upper") + 1);
         } else if (Character.isDigit(c)) {
            characterCount.put("digit", characterCount.get("digit") + 1);
         } else {
            characterCount.put("spec", characterCount.get("spec") + 1);
         }
      }
      return pwd.length() > 8 &&
              (characterCount.get("lower") >= 3) &&
              (characterCount.get("upper") >= 3) &&
              (characterCount.get("digit") >= 3) &&
              (characterCount.get("spec") >= 2);
   }
}