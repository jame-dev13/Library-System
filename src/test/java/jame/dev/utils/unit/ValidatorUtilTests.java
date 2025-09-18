package jame.dev.utils.unit;

import jame.dev.utils.ValidatorUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Validator Util class Tests")
public class ValidatorUtilTests {


   @Test
   @DisplayName("String format valid")
   public void isValidString(){
      String name = "José Ángel Maciel";
      String emailtTest = "user@example.com";
      String password = "12uefi";
      assertTrue(ValidatorUtil.isValidString(name, emailtTest, password), "Should accept everything.");
   }
}
