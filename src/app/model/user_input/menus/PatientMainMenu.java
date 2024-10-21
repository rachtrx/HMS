package app.model.user_input.menus;

import app.model.user_input.options.ViewMedicalRecordOption;
import app.service.UserService;
import java.util.ArrayList;
import java.util.Arrays;

/**
* Main menu for patient.
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-20
*/
public class PatientMainMenu extends LoggedInMenu {

    public PatientMainMenu() {
        super(
            "Patient Main Menu",
            new ArrayList<>()
        );
        this.addOptions(new ArrayList<>(Arrays.asList(new ViewMedicalRecordOption())));
    }

    @Override
    public void display() {
        System.out.println(String.format(
            "Welcome, %s!\n",
            UserService.getCurrentUser().getName()));
        super.display();
    }
}
