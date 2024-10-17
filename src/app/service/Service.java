package app.service;

import app.model.users.User;

/**
* CRUD functionality for appointments.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public abstract class Service {
    private User user;

    public Service(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
    
    public void login(User user) {
        this.user = user;
    }

    public void logout() {
        this.user = null;
    }

    public boolean isLoggedIn() {
        return this.user != null;
    }
}
