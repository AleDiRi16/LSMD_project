package it.unipi.lsmsd.util;
import org.springframework.stereotype.Component;

@Component
public class SessionManagement {

    private static SessionManagement session = null;
    private String logged = null;
    private boolean isAdmin = false;

    private SessionManagement() {}

    public static SessionManagement getInstance() {
        if(session == null) {
            session = new SessionManagement();
        }
        return session;
    }
    public void setIsAdmin(boolean isAdmin) {
        if(session == null) {
            throw new RuntimeException("Session is not active.");
        } else {
            session.isAdmin = isAdmin;
        }
    }

    public boolean getIsAdmin() {
        if(session == null) {
            throw new RuntimeException("Session is not active.");
        } else {
            return session.isAdmin;
        }
    }


    public void setLogUser(String username) {
        if(session == null) {
            throw new RuntimeException("Session is not active.");
        } else {
            session.logged = username;
        }
    }

    public String getLogUser() {
        if(session == null) {
            throw new RuntimeException("Session is not active.");
        } else {
            return session.logged;
        }
    }


}
