package app.model.users;

import app.constants.Gender;
import app.model.ISerializable;
import app.model.users.user_credentials.Password;
import app.model.users.user_credentials.Username;
import app.utils.EnumUtils;
import app.utils.LoggerUtils;
import java.util.ArrayList;
import java.util.List;

/**
* User account
*
* @author Luke Eng (@LEPK02)
* @author Rachmiel Teo
* @author Khoo Qian Yee
* @version 1.0
* @since 2024-10-17
*/
public abstract class User implements ISerializable {
    
    // public abstract void displayUserMenu();
    private static int uuid = 1;
    private final int userId;

    public static void setUuid(int value) {
        uuid = value;
    }

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
        this.userId = User.uuid++;
        this.username = new Username(username);
        this.password = new Password(password);
        this.name = name;
        this.gender = EnumUtils.fromString(Gender.class, gender);
    }

    public User(List<String> row) throws Exception {
        LoggerUtils.info(String.join(", ", row));
        this.userId = Integer.parseInt(row.get(0));
        this.username = new Username(row.get(1));
        this.password = new Password(row.get(2));
        this.name = row.get(3);
        this.gender = EnumUtils.fromString(Gender.class, row.get(4));
        User.setUuid(Math.max(User.uuid, this.userId)+1);
    }

    public List<String> serialize() {
        List<String> row = new ArrayList<>();
        row.add(String.valueOf(this.getUserId()));
        row.add(this.getUsername());
        row.add(this.getPassword());
        row.add(this.getName());
        row.add(this.getGender());
        return row;
    }

    public int getUserId() {
        return this.userId;
    }

    public abstract int getRoleId();

    // /** 
    //  * @return String
    //  */
    public String getUsername() {
        return this.username.getUsername();
    };

    public void setUsername(String username) throws Exception { // abstract password into its own class? parent class credentials?
        this.username.setUsername(username);
        update(this);
    }

    // Who should be able to access this?
    public String getPassword() {
        return this.password.getPassword();
    }

    public void setPassword(String password) throws Exception { // abstract password into its own class? parent class credentials?
        this.password.setPassword(password);
        update(this);
    }

    // /**
    //  * Get name
    //  * @return
    //  */
    public String getName() {
        return this.name;
    }

    /**
     * Set name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
        update(this);
    }

    // /**
    //  * Get gender
    //  * @return
    //  */
    public String getGender() {
        return this.gender.toString();
    }

    /**
     * Set gender
     * @param gender
     */
    public void setGender(String gender) {
        this.gender = Gender.fromString(gender);
        update(this);
    }

    @Override
    public String toString() {
        return this.getUsername();
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
