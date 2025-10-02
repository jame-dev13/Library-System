package jame.dev.utils;

import jame.dev.dtos.users.SessionDto;
import lombok.Getter;

@Getter
public final class SessionManager {
    private static SessionManager session;
    private SessionDto sessionDto;
    private SessionManager(){}

    public synchronized static SessionManager getInstance(){
        if(session == null){
            session = new SessionManager();
        }
        return session;
    }

    public void login(SessionDto sessionDto){
        this.sessionDto = sessionDto;
    }

    public void logout(){
        this.sessionDto = null;
    }
}
