package app.model.user_input.menus;

import java.util.ArrayList;
import org.w3c.dom.Text;

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
                new String[] { // Each line as an element of the array
                    "⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⣴⣶⣶⠶⠖⠲⠶⣶⣶⣦⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                    "⠀⠀⠀⠀⠀⠀⠀⠀⠀ ⢸⣿⡟⠁⠀⣶⣶⠀⠈⢻⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀",
                    "⠀⠀⣀⣀⣀⣀⣀⣀⡀⢸⣿⡇⢸⣿⣿⣿⣿⡇⢸⣿⡇⢀⣀⣀⣀⣀⣀⣀⠀⠀",
                    "⠀⠀⣿⣿⣿⣿⣿⣿⡇⢸⣿⣧⡀⠀⠿⠿⠀⢀⣼⣿⡇⢸⣿⣿⣿⣿⣿⣿⡇⠀",
                    "⠀⠀⣿⣇⣀⣿⣀⣸⡇⢸⣿⣿⣿⣷⣶⣶⣾⣿⣿⣿⡇⢸⣿⣀⣸⣇⣀⣿⡇⠀",
                    "⠀⠀⣿⡏⠉⣿⠉⢹⡇⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇⢸⣿⠉⢹⡏⠉⣿⡇⠀",
                    "⠀⠀⣿⡟⠛⣿⠛⢻⡇⢸⣿⣿⣿⠿⠿⠿⠿⢿⣿⣿⡇⢸⣿⠛⢻⡟⠛⣿⡇⠀",
                    "⠀⠀⣿⣧⣴⣿⣤⣾⡇⢸⣿⣿⣿⠀⠀⠀⠀⢸⣿⣿⡇⢸⣿⣤⣾⣧⣴⣿⡇⠀",
                    "⠀⠀⣿⣿⣿⣿⣿⣿⡇⢸⣿⣿⣿⠀⠀⠀⠀⢸⣿⣿⡇⢸⣿⣿⣿⣿⣿⣿⡇⠀",
                    "⣤⣤⣿⣿⣿⣿⣿⣿⣧⣼⣿⣿⣿⣤⣤⣤⣤⣼⣿⣿⣧⣼⣿⣿⣿⣿⣿⣿⣧⣤",
                    "⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿"
                }
            ),
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
