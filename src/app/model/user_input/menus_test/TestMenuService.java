package app.model.user_input.menus_test;

public class TestMenuService {
    private BaseMenu currentMenu;

    // Constructor to start with the login menu
    public TestMenuService() {
        this.currentMenu = new LoginMenu();
    }

    // Main loop to run the FSM
    public void run() {
        while (currentMenu != null) {
            currentMenu = currentMenu.run();  // Run the current menu and move to the next one
        }
        System.out.println("Exiting...");
    }
}