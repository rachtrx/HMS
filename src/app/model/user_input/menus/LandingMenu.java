package app.model.user_input.menus;

import java.util.ArrayList;
import org.w3c.dom.Text;

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
                " ⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⣴⣶⣶⠶⠖⠲⠶⣶⣶⣦⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀"
                " ⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⡟⠁⠀⣶⣶⠀⠈⢻⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀"
                "⠀⠀⣀⣀⣀⣀⣀⣀⡀⢸⣿⡇⢸⣿⣿⣿⣿⡇⢸⣿⡇⢀⣀⣀⣀⣀⣀⣀⠀⠀"
                "⠀⠀⣿⣿⣿⣿⣿⣿⡇⢸⣿⣧⡀⠀⠿⠿⠀⢀⣼⣿⡇⢸⣿⣿⣿⣿⣿⣿⡇⠀"
                "⠀⠀⣿⣇⣀⣿⣀⣸⡇⢸⣿⣿⣿⣷⣶⣶⣾⣿⣿⣿⡇⢸⣿⣀⣸⣇⣀⣿⡇⠀"
                "⠀⠀⣿⡏⠉⣿⠉⢹⡇⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇⢸⣿⠉⢹⡏⠉⣿⡇⠀"
                "⠀⠀⣿⡟⠛⣿⠛⢻⡇⢸⣿⣿⣿⠿⠿⠿⠿⢿⣿⣿⡇⢸⣿⠛⢻⡟⠛⣿⡇⠀"
                "⠀⠀⣿⣧⣴⣿⣤⣾⡇⢸⣿⣿⣿⠀⠀⠀⠀⢸⣿⣿⡇⢸⣿⣤⣾⣧⣴⣿⡇⠀"
                "⠀⠀⣿⣿⣿⣿⣿⣿⡇⢸⣿⣿⣿⠀⠀⠀⠀⢸⣿⣿⡇⢸⣿⣿⣿⣿⣿⣿⡇⠀"
                "⣤⣤⣿⣿⣿⣿⣿⣿⣧⣼⣿⣿⣿⣤⣤⣤⣤⣼⣿⣿⣧⣼⣿⣿⣿⣿⣿⣿⣧⣤"
                "⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿⠿"
            )
        );
    }
}
