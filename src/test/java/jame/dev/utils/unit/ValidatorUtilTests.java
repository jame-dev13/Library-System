package jame.dev.utils.unit;

import jame.dev.utils.ValidatorUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Validator Util class Tests")
public class ValidatorUtilTests {


   @Test
   @DisplayName("Valid String")
   public void isValidString(){
      Assertions.assertTrue(ValidatorUtil.isValidString("Hi i'm a valid String"), "Should pass because it just contains letters");
      Assertions.assertFalse(ValidatorUtil.isValidString("SELECT * FROM users WHERE 1 = 1;"), "Should fail because it contains especial chars not allowed.");
   }

   @Test
   @DisplayName("Valid usernames")
   public void isValidUsername(){
      Assertions.assertTrue(ValidatorUtil.isValidString("angelote13", "angelo2.dev", "jame.dev013", "jam.dev13"), "Should pass because it meets the requirements.");
      Assertions.assertFalse(ValidatorUtil.isValidString("0angel", "1angelo2", "?angel"), "Should fail because it should starts with a letter.");
   }

   @Test
   @DisplayName("Valid email address")
   public void isValidEmail(){
      Assertions.assertTrue(ValidatorUtil.isEmailValid("jose.maciel5968@alumnos.udg.mx"),
              "Should pass, it's a valid email address.");
      Assertions.assertTrue(ValidatorUtil.isEmailValid("angelo123@mail.org.net"),
              "Should pass, it's a valid format for an email address.");
      Assertions.assertFalse(ValidatorUtil.isEmailValid("1angel234@ainx.com"),
              "Should fail, it starts with a number");
      Assertions.assertFalse(ValidatorUtil.isEmailValid(".sww@ainx.com"),
              "Should fail, it starts with '.'.");
   }

   @Test
   @DisplayName("Valid Password")
   public void isValidPassword(){
      Assertions.assertTrue(ValidatorUtil.validPassword("abcABC123$#."),
              "Should pass because it meets has length grater than 11, 3 lower, 3 upper, 2 or more digits and 2 or more spec chars.");
      Assertions.assertFalse(ValidatorUtil.validPassword("abAB1234564#"), "Should fail because it doesn't meet the requirements.");
      Assertions.assertFalse(ValidatorUtil.validPassword("abc"), "Should fail cause the length is less than 11.");
      Assertions.assertFalse(ValidatorUtil.validPassword("29f42 f402f4"), "Should fail cause white space is present.");
      Assertions.assertFalse(ValidatorUtil.validPassword(""), "Should fail cause is blank.");
      Assertions.assertFalse(ValidatorUtil.validPassword(null), "Should fail cause is null.");
   }
}
