package app.model.user_input.menus_test;

import app.model.users.User;
import java.util.Scanner;

public abstract class BaseMenu {

    protected static Scanner scanner = new Scanner(System.in);

    protected static User currentUser;
    
    // Method to display the menu
    abstract public void display();

    // Method to handle the input and return the next menu (or null to exit)
    abstract public BaseMenu run();
}