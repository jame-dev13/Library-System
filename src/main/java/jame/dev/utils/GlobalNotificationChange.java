package jame.dev.utils;

import java.util.HashMap;
import java.util.Map;

public class GlobalNotificationChange {
   private static volatile GlobalNotificationChange instance;
   private static final Map<String, Integer> changes = new HashMap<>();
   private GlobalNotificationChange(){}

   public synchronized static GlobalNotificationChange getInstance(){
      if(instance == null)
         instance = new GlobalNotificationChange();
      return instance;
   }

   public Map<String, Integer> getChanges(){
      return changes;
   }

   public void registerChange(String classNameChange){
      changes.put(classNameChange, changes.getOrDefault(classNameChange, 0) + 1);
   }
}
