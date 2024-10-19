package app.model.user_input.menus;

/**
* User login - enter username.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class LoginUsernameMenu extends BaseInputMenu {
    public LoginUsernameMenu() {
        super("Please enter your username: ");
    }

    @Override
    public BaseMenu next(String userInput) throws Exception {
        return new LoginPasswordMenu(userInput);
    }
}
