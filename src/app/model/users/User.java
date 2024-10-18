package app.model.users;

import app.constants.Gender;
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
public abstract class User {
    
    // public abstract void displayUserMenu();
    protected int id;
    protected Username username;
    protected Password password;
    protected String name;
    protected Gender gender;

    /** 
     * Constructor
     * @param username
     * @param password
     * @param name
     * @param gender
     */
    public User(String username, String password, String name, String gender) throws Exception {
        this.id = this.generateUUID();
        this.username = new Username(username);
        this.password = new Password(password);
        this.name = name;
        this.gender = Gender.FEMALE;
    }

    protected abstract int generateUUID();

    private int getId() {
        return this.id;
    }

    // /** 
    //  * @return String
    //  */
    public String getUsername() {
        return this.username.getUsername();
    };

    // public void setUsername(String username) { // abstract password into its own class? parent class credentials?
    //     /*
    //      * TODO: username validation --> handle here? or in UserService?
    //      * 
    //      * 2. Check for other existing usernames
    //      * 3. Clean input (escape characters, strip string)
    //      */
    //     this.username = username;
    // }

    // Who should be able to access this?
    public String getPassword() {
        return this.password.getPassword();
    }

    // public void setPassword(Password password) { // abstract password into its own class? parent class credentials?
    //     /*
    //      * TODO: password validation --> handle here? or in UserService?
    //      * 1. Username rules (e.g. min length, allowed characters [A-Za-z0-9],
    //      * special characters, min lower min upper, etc.)
    //      * 2. Clean input (escape characters, strip string)
    //      */
    //     this.password = password;
    // }

    // /**
    //  * Get name
    //  * @return
    //  */
    public String getName() {
        return this.name;
    }

    // /**
    //  * Set name
    //  * @param name
    //  */
    // public void setName(String name) {
    //     this.name = name;
    // }

    // /**
    //  * Get gender
    //  * @return
    //  */
    public String getGender() {
        return this.gender.toString();
    }

    // /**
    //  * Set gender
    //  * @param gender
    //  */
    // public void setGender(Gender gender) {
    //     this.gender = gender;
    // }

    // // TODO: implement
    // public void login(String username, String password) {

    // }

    // @Override
    // public boolean equals(Object o) {
    //     if (this == o) return true;
    //     if (o == null || getClass() != o.getClass()) return false;
    //     User user = (User) o;
    //     return Objects.equals(hospitalId, user.hospitalId) &&
    //        Objects.equals(password, user.password);
    // }
}
