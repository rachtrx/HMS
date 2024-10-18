package app.model.user_input.menus_test.options_menus_test;

import app.model.user_input.menus_test.BaseMenu;
import java.util.List;
import java.util.Scanner;

// Abstract OptionsMenu class with options handling
public abstract class OptionsMenu<T> extends BaseMenu {
    protected List<T> options;  // Options for the menu
    protected Scanner scanner = new Scanner(System.in);

    @Override
    public void display() {
        System.out.println("==== " + getMenuTitle() + " ====");
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
    }

    @Override
    public BaseMenu run() {
        display();
        System.out.print("Select an option by entering the number: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (validateOption(choice)) {
            return getNextMenu(choice);  // Return the next menu based on the selection
        } else {
            System.out.println("Invalid selection. Try again.");
            return this;  // Stay in the current menu if selection is invalid
        }
    }

    // Abstract method to be implemented by subclasses to define the menu title
    protected abstract String getMenuTitle();

    // Abstract method to validate the option (to be implemented by subclasses)
    protected abstract boolean validateOption(int choice);

    // Abstract method to define the next menu based on the selected option
    protected abstract BaseMenu getNextMenu(int choice);
}