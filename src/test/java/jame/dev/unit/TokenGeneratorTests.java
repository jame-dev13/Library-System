package jame.dev.unit;

import jame.dev.utils.TokenGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Token Generator Tests")
public class TokenGeneratorTests {

   @Test
   @DisplayName("Non Null")
   public void notNull(){
      String token = TokenGenerator.genToken();
      assertNotNull(token, "The token shouldn't be null");
   }

   @Test
   @DisplayName("Token length is 6 characters of length")
   public void length(){
      String token = TokenGenerator.genToken();
      assertEquals(6, token.length(), "The length for the token should be of 6 characters.");
   }

   @Test
   @DisplayName("All token has length of 6")
   public void lengthAllIsSix(){
      List<String> tokens = Stream
              .generate(TokenGenerator::genToken)
              .limit(1000)
              .toList();
      assertTrue(tokens.parallelStream().allMatch(tk -> tk.length() == 6));
   }
}
