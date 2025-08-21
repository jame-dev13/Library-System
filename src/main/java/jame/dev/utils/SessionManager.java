package jame.dev.utils;

import lombok.Getter;

@Getter
public class SessionManager {
    private static volatile SessionManager session;
    private String username;
    private SessionManager(){}

    public static SessionManager getInstance(){
        if(session == null){
            synchronized (SessionManager.class){
                if(session == null) session = new SessionManager();
            }
        }
        return session;
    }

    public void login(String username){
        this.username = username;
    }

    public void logout(){
        this.username = null;
        session = null;
    }
}
