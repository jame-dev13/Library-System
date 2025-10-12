package jame.dev.utils.tools;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class provides a static method to get a random secure token of characters alphanumerics.
 * The main stream is, given a string of chars and a {@link SecureRandom} object
 * generate a random string token of length 6.
 */
public final class TokenGenerator {

   private static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
   private static final SecureRandom random = new SecureRandom();

   /**
    * Generates a {@link IntStream} of range 0 to 6, gets the value of a String
    * for a random selected index form the chars array, and then joins it using {@link Collectors}.{@code joining()}
    * @return a random token of 6 characters.
    */
   public static String genToken() {
      return IntStream.range(0, 6)
              .mapToObj(_ ->
                      String.valueOf(chars.charAt(random.nextInt(chars.length()))))
              .collect(Collectors.joining());
   }
}
