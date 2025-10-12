package jame.dev.utils.session;

import jame.dev.dtos.users.SessionDto;
import lombok.Getter;

/**
 * This class handles the basic behavior of a Session.
 * <p><b>Note: </b>This is a singleton class.</p>
 */
@Getter
public final class SessionManager {
    private static SessionManager session;
    private SessionDto sessionDto;
    private SessionManager(){}

   /**
    * Gets the current and unique existing instance of this class.
    * if it is null, then the instance is created.
    * @return the instance of @{code {@link SessionManager}}
    */
    public synchronized static SessionManager getInstance(){
        if(session == null){
            session = new SessionManager();
        }
        return session;
    }

   /**
    * Sets a {@link SessionDto} entry to the intern {@code SessionDto}.
    * @param sessionDto the entry {@code SessionDto}.
    */
    public void login(SessionDto sessionDto){
        this.sessionDto = sessionDto;
    }

   /**
    * Set the value of the sessionDto intern object to null.
    */
   public void logout(){
        this.sessionDto = null;
    }
}
