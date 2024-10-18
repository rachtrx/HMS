package app.model.user_input.menus_test;

import app.constants.AppMetadata;
import app.model.user_input.menus_test.options_menus_test.AppointmentMenu;
import app.service.UserService;

public class LoginMenu extends BaseMenu {

    @Override
    public void display() {
        System.out.println(
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

    @Override
    public BaseMenu run() {
        display();

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        currentUser = UserService.findUser(username, password);
        if (currentUser != null) {
            System.out.println("Login Successful!");
            return new AppointmentMenu();
        } else {
            System.out.println("Invalid credentials. Try again.");
            return this;
        }
    }
}