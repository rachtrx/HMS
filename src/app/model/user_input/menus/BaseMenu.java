package app.model.user_input.menus;

import app.utils.StringUtils;
import java.util.stream.IntStream;

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

    public void displayTitle() {
        if (this.getTitle() != null) {
            System.out.println(this.getTitle());
        }
        IntStream.range(0, 30).forEach(n -> System.out.print("-"));
        System.out.println("");
    }

    public abstract void display();

    public abstract BaseMenu next(String userInput) throws Exception;

    public BaseMenu handleUserInput(String userInput) throws Exception {
        return this.handleUserInput(userInput, true);
    }

    protected final BaseMenu handleUserInput(String userInput, boolean parseString) throws Exception {
        return this.next(parseString ? StringUtils.parseUserInput(userInput) : userInput);
    };
}