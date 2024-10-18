package app.model.user_input.menus;

import app.constants.AppMetadata;
import java.util.ArrayList;

import app.model.user_input.options.ExitOption;

/**
* Initial landing page.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class LandingMenu extends BaseMenu {
    public LandingMenu() {
        super(
            new ArrayList<>(),
            String.join(
                "\n",
                " ⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⣴⣶⣶⠶⠖⠲⠶⣶⣶⣦⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                " ⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⡟⠁⠀⣶⣶⠀⠈⢻⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                "⠀⠀⣀⣀⣀⣀⣀⣀⡀⢸⣿⡇⢸⣿⣿⣿⣿⡇⢸⣿⡇⢀⣀⣀⣀⣀⣀⣀⠀⠀",
                "⠀⠀⣿⣿⣿⣿⣿⣿⡇⢸⣿⣧⡀⠀⠿⠿⠀⢀⣼⣿⡇⢸⣿⣿⣿⣿⣿⣿⡇⠀",
                "⠀⠀⣿⣇⣀⣿⣀⣸⡇⢸⣿⣿⣿⣷⣶⣶⣾⣿⣿⣿⡇⢸⣿⣀⣸⣇⣀⣿⡇⠀",
                "⠀⠀⣿⡏⠉⣿⠉⢹⡇⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇⢸⣿⠉⢹⡏⠉⣿⡇⠀",
                "⠀⠀⣿⡟⠛⣿⠛⢻⡇⢸⣿⣿⣿⠿⠿⠿⠿⢿⣿⣿⡇⢸⣿⠛⢻⡟⠛⣿⡇⠀",
                "⠀⠀⣿⣧⣴⣿⣤⣾⡇⢸⣿⣿⣿⠀⠀⠀⠀⢸⣿⣿⡇⢸⣿⣤⣾⣧⣴⣿⡇⠀",
                "⠀⠀⣿⣿⣿⣿⣿⣿⡇⢸⣿⣿⣿⠀⠀⠀⠀⢸⣿⣿⡇⢸⣿⣿⣿⣿⣿⣿⡇⠀",
                "⣤⣤⣿⣿⣿⣿⣿⣿⣧⣼⣿⣿⣿⣤⣤⣤⣤⣼⣿⣿⣧⣼⣿⣿⣿⣿⣿⣿⣧⣤",
                "⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿",
                String.format(
                    "\nWelcome to the %s (%s)!",
                    AppMetadata.APP_FULL_NAME.toString(),
                    AppMetadata.APP_SHORT_NAME.toString()
                )
            )
        );
    }

    ExitOption exitOption = new ExitOption(
    "Exit Application",
    null, // assuming no next menu on exit
    userInput -> {
        System.exit(0);
        return null;
    }
);
}
