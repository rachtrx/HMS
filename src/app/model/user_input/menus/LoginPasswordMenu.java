package app.model.user_input.menus;

/**
* User login - enter password.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class LoginPasswordMenu extends BaseInputMenu {
    private String username;
    public LoginPasswordMenu(String username) {
        super("Please enter your password: ");
        this.username = username;
    }

    @Override
    public BaseMenu next(String userInput) throws Exception {
        // TODO: new LoggedInMenu abstract class -> add default Logout option
        // TODO: new menu for each user (e.g. patient, doctor)
        // TODO: check username is in DB --> if yes, move to a LoggedInMenu.
        // 
        // if not, return LoginUsernameMenu
        // MenuService.setCurrentMenu(new LoginUsernameMenu());
        // throw new Exception("Incorrect username or password. Please try again.");
        return new LandingMenu();
    }
}
