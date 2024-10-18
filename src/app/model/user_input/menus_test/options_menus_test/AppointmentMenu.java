package app.model.user_input.menus_test.options_menus_test;
import app.model.user_input.menus_test.BaseMenu;
import java.util.Arrays;

public class AppointmentMenu extends OptionsMenu<String> {

    // Constructor to initialize the appointment options
    public AppointmentMenu() {
        this.options = Arrays.asList("20/10/2024", "24/10/2024", "25/10/2024");
    }

    @Override
    protected String getMenuTitle() {
        return "Book an Appointment";  // Title of the menu
    }

    @Override
    protected boolean validateOption(int choice) {
        // Validate that the choice is within the valid range
        return choice > 0 && choice <= options.size();
    }

    @Override
    protected BaseMenu getNextMenu(int choice) {
        // Process the selected appointment option
        System.out.println("You selected: " + options.get(choice - 1));
        // Logic for booking the appointment (placeholder)
        System.out.println("Appointment booked successfully!");
        return null;  // Return null to indicate the end of the process
    }
}