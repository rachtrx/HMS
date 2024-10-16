package app.model.personal_details;

import app.constants.Gender;

/**
* Name and gender.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class BasicPersonalDetails {
    protected String name;
    protected Gender gender;
    
    /**
     * Constructor
     * @param name
     * @param gender
     */
    public BasicPersonalDetails(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
    }

    /**
     * Get name
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get gender
     * @return
     */
    public Gender getGender() {
        return this.gender;
    }

    /**
     * Set gender
     * @param gender
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
