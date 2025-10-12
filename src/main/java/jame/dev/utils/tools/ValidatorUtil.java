package jame.dev.utils.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that provides validations methods for given entries od type string
 */
public final class ValidatorUtil {
   /**
    * Validates if the entry values matches with a regex witch evaluates that the string
    * contains only characters alphanumeric, and some special characters. The point is the input
    * values do match with expected values like "user123", "user last name", "user@123", etc.
    * @param s the string to evaluate.
    * @return true if matches, false if it not.
    */
   public static boolean isValidName(String... s) {
      if (s == null || s.length == 0) return false;
      for (String string : s) {
         if (string == null || string.isBlank() || !string.matches("^[a-zA-záéíóúÁÉÍÓÚñÑ][a-zA-Z0-9áéíóúÁÉÍÓÚñÑ@'.-_ ]+$")) {
            return false;
         }
      }
      return true;
   }

   /**
    * Evaluates if the given string matches with the main characters of an internet email address.
    * @param s the given string
    * @return true if {@code s} matches, false if it not.
    */
   public static boolean isEmailValid(String s) {
      return s.matches("^[a-zA-Z][a-zA-Z0-9.-_]+@+[a-zA-Z0-9.-]+\\.[a-zA-Z0-9]{2,}$");
   }

   /**
    * Evaluates if the given {@code pwd} is strong or not, to be considered as 'strong' one
    * the pwd must contain at least 3 or more lower case letters, 3 or more upper case
    * letters, 3 or more digits, 2 or more special characters and his length is greater than 8.
    * @param pwd the pwd string to evaluate.
    * @return true if the pwd is strong, false if it not.
    */
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