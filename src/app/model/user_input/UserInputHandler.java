package app.model.user_input;

import java.util.Scanner;

/**
* Menu option.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/
public class UserInputHandler {
    private final static Scanner scanner = new Scanner(System.in);
    public String getUserInput() {
        String userInput = UserInputHandler.scanner.nextLine();
    }
}