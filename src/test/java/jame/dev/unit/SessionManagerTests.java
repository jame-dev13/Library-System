package jame.dev.unit;

import jame.dev.dtos.SessionDto;
import jame.dev.models.enums.ERole;
import jame.dev.utils.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Session Manager Tests")
public class SessionManagerTests {

   private SessionManager sessionManager;

   @BeforeEach
   void setUp(){
      sessionManager = SessionManager.getInstance();
      sessionManager.logout(); //clean state
   }

   @Test
   @DisplayName("Singleton")
   public void uniqueInstance() {
      SessionManager instance2 = SessionManager.getInstance();
      assertSame(sessionManager, instance2,"The instances must be the same one.");
   }

   @Test
   @DisplayName("Login")
   public void login(){
      SessionDto sessionDto = SessionDto.builder()
              .id(1)
              .email("test1@example.com")
              .role(ERole.ADMIN)
              .build();
      this.sessionManager.login(sessionDto);
      SessionDto dtoNotNull = this.sessionManager.getSessionDto();
      assertNotNull(dtoNotNull, "The sessionDto shouldn't be null");
   }

   @Test
   @DisplayName("Logout")
   public void logout(){
      SessionDto sessionDto = SessionDto.builder()
              .id(1)
              .uuid(UUID.randomUUID())
              .email("test1@example.com")
              .role(ERole.ADMIN)
              .build();
      this.sessionManager.login(sessionDto);
      this.sessionManager.logout();
      assertNull(this.sessionManager.getSessionDto(), "The SessionDto should be null after logout.");
   }

   @Test
   @DisplayName("before Login")
   public void beforeLogin() {
      assertNull(this.sessionManager.getSessionDto(), "The SessionDto should be null before login.");
   }

   @Test
   @DisplayName("Should be thread Safe")
   public void isThreadSafe() throws InterruptedException, ExecutionException {
      int numThreads = 100;
      ExecutorService executor = Executors.newFixedThreadPool(numThreads);

      Callable<SessionManager> task = SessionManager::getInstance;
      Set<Future<SessionManager>> futures = new HashSet<>();
      for (int i = 0; i < numThreads; i++) {
         futures.add(executor.submit(task));
      }

      executor.shutdown();
      executor.awaitTermination(5, TimeUnit.SECONDS);

      SessionManager reference = null;
      for (Future<SessionManager> future : futures) {
         SessionManager result = future.get();
         if(reference == null){
            reference = result;
         }else{
            assertSame(reference, result, "Different instances are detected");
         }
      }

   }
}
