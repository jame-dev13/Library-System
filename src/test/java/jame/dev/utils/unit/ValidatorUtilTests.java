package jame.dev.utils.unit;

import jame.dev.utils.tools.ValidatorUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Validator Util class Tests")
public class ValidatorUtilTests {


   @Test
   @DisplayName("Valid String")
   public void isValidName(){
      Assertions.assertTrue(ValidatorUtil.isValidName("Hi i'm a valid String"), "Should pass because it just contains letters");
      Assertions.assertFalse(ValidatorUtil.isValidName("SELECT * FROM users WHERE 1 = 1;"), "Should fail because it contains especial chars not allowed.");
   }

   @Test
   @DisplayName("Valid usernames")
   public void isValidUsername(){
      Assertions.assertTrue(ValidatorUtil.isValidName("angelote13", "angelo2.dev", "jame.dev013", "jam.dev13"), "Should pass because it meets the requirements.");
      Assertions.assertFalse(ValidatorUtil.isValidName("0angel", "1angelo2", "?angel"), "Should fail because it should starts with a letter.");
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
   @DisplayName("Is pwd strong")
   public void isStrongPwd(){
      Assertions.assertTrue(ValidatorUtil.pwdIsStrong(".RDFs5067al!"),
              "It Should pass if pwd has at least 3 or more lower, 3 upper, 3 digit and 2 or more especial chars and his length it's greater than 8");
      Assertions.assertFalse(ValidatorUtil.pwdIsStrong("ghTY12.-"),
              "Should fail, 3 upper, 3 lower, 3 digit and 2 or more spec chars are required to have a pwd 'strong'.");
   }
}
