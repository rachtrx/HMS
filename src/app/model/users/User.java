package app.model.users;

import app.constants.Gender;
import app.model.personal_details.BasicPersonalDetails;
import app.model.user_credentials.Password;
import app.model.user_credentials.Username;

/**
* User account
*
* @author Luke Eng (@LEPK02)
* @author Rachmiel Teo
* @author Khoo Qian Yee
* @version 1.0
* @since 2024-10-17
*/
public abstract class User extends BasicPersonalDetails {
    
    public abstract void displayUserMenu();
    protected int id;
    protected Username username;
    protected Password password;

    /** 
     * Constructor
     * @param username
     * @param password
     * @param name
     * @param gender
     */
    public User(Username username, Password password, String name, Gender gender) {
        super(name, gender);
        this.id = this.generateUUID();
        this.username = username;
        this.password = password;
    }

    protected abstract int generateUUID();

    
    /** 
     * @return String
     */
    public Username getUsername() {
        return this.username;
    };

    public void setUsername(Username username) { // abstract password into its own class? parent class credentials?
        /*
         * TODO: username validation --> handle here? or in UserService?
         * 
         * 2. Check for other existing usernames
         * 3. Clean input (escape characters, strip string)
         */
        this.username = username;
    }

    // Who should be able to access this?
    // public String getPassword() {
    //     return this.getPassword();
    // }

    public void setPassword(Password password) { // abstract password into its own class? parent class credentials?
        /*
         * TODO: password validation --> handle here? or in UserService?
         * 1. Username rules (e.g. min length, allowed characters [A-Za-z0-9],
         * special characters, min lower min upper, etc.)
         * 2. Clean input (escape characters, strip string)
         */
        this.password = password;
    }

    // TODO: implement
    public void login(String username, String password) {

    }

    // @Override
    // public boolean equals(Object o) {
    //     if (this == o) return true;
    //     if (o == null || getClass() != o.getClass()) return false;
    //     User user = (User) o;
    //     return Objects.equals(hospitalId, user.hospitalId) &&
    //        Objects.equals(password, user.password);
    // }
}
