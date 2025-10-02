package jame.dev.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
      changes.put(classNameChange, changes.getOrDefault(classNameChange, 1) + 1);
   }

   public void doneChange(String className){
      Optional.ofNullable(className).orElseThrow(NullPointerException::new);
      changes.remove(className);
   }
}
