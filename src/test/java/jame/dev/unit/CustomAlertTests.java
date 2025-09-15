package jame.dev.unit;

import jame.dev.utils.CustomAlert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertSame;

@DisplayName("Custom Alert tests")
public class CustomAlertTests {

   private CustomAlert customAlert;

   @BeforeEach
   void setUp(){
      this.customAlert = CustomAlert.getInstance();
   }

   @Test
   @DisplayName("Same instance")
   public void sameInstance(){
      CustomAlert instance = CustomAlert.getInstance();
      assertSame(customAlert, instance, "The instance should be the same in singleton");
   }

   @Test
   @DisplayName("Is thread safe")
   public void threadSafeInstance() throws InterruptedException, ExecutionException{
      int nInstances = 100;
      ExecutorService executor = Executors.newFixedThreadPool(nInstances);

      Callable<CustomAlert> task = CustomAlert::getInstance;
      Set<Future<CustomAlert>> futures = new HashSet<>();
      for (int i = 0; i < nInstances; i++) {
         futures.add(executor.submit(task));
      }

      executor.shutdown();
      executor.awaitTermination(5, TimeUnit.SECONDS);

      CustomAlert reference = null;
      for (Future<CustomAlert> future : futures) {
         CustomAlert result = future.get();
         if(reference == null)
            reference = result;
         else {
            assertSame(reference, result, "instances are not the same, singleton it's not thread safe.");
         }
      }
   }
}
