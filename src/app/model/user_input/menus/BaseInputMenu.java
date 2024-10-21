package app.model.user_input.menus;

/**
* Menu shown to users. (Equivalent to state in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public abstract class BaseInputMenu extends BaseMenu {

    private final String label;

    public BaseInputMenu(String title, String label) {
        super(title);
        this.label = label;
    }

    @Override
    public void display() {
        this.displayTitle();
        System.out.print(this.label);
    };

    public void validateUserInput(String userInput) throws Exception {
        if (userInput.length() <= 0) {
            throw new Exception("Please type something in:");
        }
    };

    public abstract BaseMenu nextMenu(String userInput) throws Exception;

    @Override
    public BaseMenu next(String userInput) throws Exception {
        this.validateUserInput(userInput);
        return this.nextMenu(userInput);
    }
}