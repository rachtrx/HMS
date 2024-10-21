package app.model.user_input.menus;

import app.service.MenuService;
import app.service.UserService;

/**
* User login - enter password.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class LoginPasswordMenu extends BaseInputMenu {
    private final String username;
    public LoginPasswordMenu(String username) {
        super("Login", "Please enter your password: ");
        this.username = username;
    }

    @Override
    public BaseMenu handleUserInput(String userInput) throws Exception {
        return this.handleUserInput(userInput, false);
    }

    @Override
    public BaseMenu nextMenu(String userInput) throws Exception {
        // TODO: Implement Logic - user and password validation
        // Check username is in DB --> if yes, move to a LoggedInMenu.
        // 
        // if not, return LoginUsernameMenu
        // MenuService.setCurrentMenu(new LoginUsernameMenu());
        // throw new Exception("Incorrect username or password. Please try again.");
        try {
            UserService.login(this.username, userInput);
            return MenuService.getLoggedInUserMenu(UserService.getCurrentUser());
        } catch (Exception e) {
            System.out.println(e.getMessage() + "\n");
            return new LoginUsernameMenu();
        }
    }
}
