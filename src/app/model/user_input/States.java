package app.model.user_input;

import java.util.stream.IntStream;

import app.constants.AppMetadata;

/**
* Menu.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-21
*/
public enum States {
    LANDING(
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
            ), "\nPress 'Enter' to continue...\n\n"
        ), ""
    ), LOGIN_USERNAME("Please enter your username: ", "Login");

    private final String menu;
    private final String title;

    States(String menu, String title) {
        this.menu = menu;
        this.title = title;
    }

    public void printMenu() {
        System.out.println(this.title);
        IntStream.range(0, 30).forEach(n -> System.out.print("-"));
        System.out.println("");
        System.out.print(this.menu);
    }
}
