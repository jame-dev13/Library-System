package jame.dev.utils;

public final class ValidatorUtil {

   public static boolean isValidString(String... s) {
      if(s == null || s.length == 0) return false;
      for (String string : s) {
         if(string == null || string.isBlank() || !string.matches("^[a-zA-záéíóúÁÉÍÓÚñÑ][a-zA-Z0-9áéíóúÁÉÍÓÚñÑ@'. ]+$")){
            return false;
         }
      }
      return true;
   }

   public static boolean isEmailValid(String s) {
      return s.matches("^[a-zA-Z][a-zA-Z0-9.-_]+@+[a-zA-Z0-9.-]+\\.[a-zA-Z0-9]{2,}$");
   }

   public static boolean isValidPassword(String s) {
      final int MIN_LENGTH = 11;
      final int MIN_UPPER = 3;
      final int MIN_LOWER = 3;
      final int MIN_DIGIT = 2;
      final int MIN_SPEC = 2;

      if (s == null || s.isBlank() || s.length() < MIN_LENGTH) {
         return false;
      }

      int upper = 0, lower = 0, digit = 0, spec = 0;

      for (char c : s.toCharArray()) {
         if (Character.isSpaceChar(c))
            return false;
         else if (Character.isUpperCase(c)) {
            upper++;
         } else if (Character.isLowerCase(c)) {
            lower++;
         } else if (Character.isDigit(c)) {
            digit++;
         } else {
            spec++;
         }

         if (upper >= MIN_UPPER && lower >= MIN_LOWER && digit >= MIN_DIGIT && spec >= MIN_SPEC) {
            return true;
         }
      }

      return upper >= MIN_UPPER && lower >= MIN_LOWER && digit >= MIN_DIGIT && spec >= MIN_SPEC;
   }

}