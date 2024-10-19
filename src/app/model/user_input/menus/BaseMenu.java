package app.model.user_input.menus;
/**
* Menu shown to users. (Equivalent to state in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public abstract class BaseMenu {

    protected String title;

    public BaseMenu(String title) {
        this.title = title;
    }

    /** 
     * @return String
     */
    public String getTitle() {
        return this.title;
    }

    public abstract void display();

    public abstract BaseMenu next(String userInput) throws Exception;
}