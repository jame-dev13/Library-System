package jame.dev.utils.session;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to communicates changes along the entire app.
 * it implements a HashMap to save the changes and with this we can know
 * where the change happen and handle it.
 * <p><b>Note: </b>This is a Singleton class.</p>
 */
public final class GlobalNotificationChange {
   private static volatile GlobalNotificationChange instance;
   private static final Map<String, Integer> changes = new HashMap<>();
   private GlobalNotificationChange(){}

   /**
    * Get the current and unique instance if this class along the program.
    * If it is null then create the instance.
    * @return the instance of {@code GlobalNotificationChange}.
    */
   public synchronized static GlobalNotificationChange getInstance(){
      if(instance == null)
         instance = new GlobalNotificationChange();
      return instance;
   }

   /**
    * Get the intern HashMap called changes.
    * @return the changes HashMap.
    */
   public Map<String, Integer> getChanges(){
      return changes;
   }

   /**.
    * Simply add change into changes Map.
    * @param changeName the name of the change.
    */
   public void registerChange(String changeName){
      changes.put(changeName, changes.getOrDefault(changeName, 1) + 1);
   }
}
