package app.model.user_input.menus;

import app.constants.AppMetadata;

/**
* Initial landing page.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class LandingMenu extends BaseInputMenu {
    public LandingMenu() {
        super(
            "",
            String.join(
                "\n",
                "| |  | |  \\/  |/ ____|",
                "| |__| | \\  / | (___  ",
                "|  __  | |\\/| |\\___ \\ ",
                "| |  | | |  | |____) |",
                "|_|  |_|_|  |_|_____/ ",
                String.format(
                    "\nWelcome to the %s (%s)!",
                    AppMetadata.APP_FULL_NAME.toString(),
                    AppMetadata.APP_SHORT_NAME.toString()
                ), "\nPress 'Enter' to continue..."
            )
        );
    }

    @Override
    public void displayTitle() {}

    @Override
    public void validateUserInput(String userInput) throws Exception {}

    @Override
    public BaseMenu nextMenu(String userInput) throws Exception {
        // Any action goes here
        return new LoginUsernameMenu();
    }
}
