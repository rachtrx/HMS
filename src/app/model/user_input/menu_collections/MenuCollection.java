package app.model.user_input.menu_collections;

import app.constants.AppMetadata;
import app.model.user_input.InputMenu;
import app.model.user_input.Menu;
import app.model.user_input.MenuState;
import app.model.user_input.OptionMenu;
import app.model.user_input.menu_collections.MenuCollection.Control;
import app.model.user_input.option_collections.OptionGeneratorCollection;
import app.model.users.User;
import app.model.users.user_credentials.Password;
import app.service.MenuService;
import app.service.UserService;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MenuCollection {

    public enum Control {
        ADD,
        EDIT,
        DELETE,
        APPROVE,
        REJECT,
        SELECT,
        NONE
    }
    
    public static Menu getEditMenu() {
        return new InputMenu("Edit Menu", "Enter a new value: ").setParseUserInput(false);
    }

    public static Menu getConfirmMenu() {
        return new OptionMenu("Confirm Menu", "Please confirm your decision: ");
    }

    public static Menu getLandingMenu() {
        InputMenu menu = new InputMenu("Landing Menu", String.join(
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
            ), "\nPress 'Enter' to continue..."
        ));
        menu
            .getInput()
            .setNextAction((formData) -> new HashMap<>())
            .setNextMenuState(MenuState.LOGIN_USERNAME)
            .setExitMenuState(MenuState.LANDING);

        return menu;
    }

    public static Menu getLoginUsernameMenu() {
        InputMenu menu = new InputMenu("Login Username Menu", "Please enter your username");

        menu
            .getInput()
            .setNextMenuState(MenuState.LOGIN_PASSWORD)
            .setNextAction((formData) -> new HashMap<String, Object>() {{
                put("username", formData.get("input"));
            }});
        return menu;
    }

    public static Menu getLoginPasswordMenu() {
        InputMenu menu = new InputMenu("Login Password Menu", null)
            .setParseUserInput(false);
        menu
            .getInput()
            .setNextMenuState(null)
            .setNextAction((formData) -> {
                try {
                    String password = (String) formData.get("input");
                    if (UserService.getCurrentUser() == null) {
                        UserService.login((String) formData.get("username"), password);
                    } else {
                        UserService.getCurrentUser().setPassword(password);
                    }
                    return null;
                } catch (Exception e) {
                    throw e;
                }
            });
        return menu;
    }

    public static Menu getChangePasswordMenu() {
        InputMenu menu = new InputMenu("Change Password Menu", "Please enter your password")
            .setParseUserInput(false);
        menu
            .getInput()
            .setNextMenuState(null)
            .setExitMenuState(MenuState.LOGIN_USERNAME)
            .setNextAction((formData) -> {
                try {
                    UserService.login((String) formData.get("username"), (String) formData.get("input"));
                    return null;
                } catch (Exception e) {
                    return null;
                }
            });
        return menu;
    }

    // The following are shared by Doctors and Patients
    public static Menu getTimeSlotSelectionMenu() {
        return new OptionMenu("Select a Date", null)
            .setOptionGenerator(() -> OptionGeneratorCollection.generateTimeSlotSelectOptions())
            .shouldAddMainMenuOption();
    }

    public static Menu getInputAppointmentYearMenu() {
        return new OptionMenu("Choose Year", null)
            .setOptionGenerator(() -> OptionGeneratorCollection.getInputYearOptionGenerator())
            .shouldAddMainMenuOption();
    }

    public static Menu getInputAppointmentMonthMenu() {
        OptionMenu menu = new OptionMenu("Choose a Month", null);

        menu.setOptionGenerator(() -> OptionGeneratorCollection.getInputMonthOptionGenerator(menu.getFormData()))
            .shouldAddMainMenuOption();
        return menu;
    }

    public static Menu getInputAppointmentDayMenu() {
        InputMenu menu = new InputMenu("Enter a day from selected range", "");

        menu.getInput()
            .setNextMenuState(MenuState.INPUT_APPOINTMENT_HOUR)
            .setNextAction((formValues) -> {
                int startDay = Integer.parseInt(formValues.get("startDay").toString());
                int endDay = Integer.parseInt(formValues.get("endDay").toString());
                int day = Integer.parseInt(formValues.get("input").toString());
                
                if (day < startDay || day > endDay) {
                    throw new IllegalArgumentException(
                        String.format("Please enter a valid date between %d and %d", startDay, endDay)
                    );
                }
                formValues.put("day", String.valueOf(day));
                return formValues;
            });
        return menu;
    }

    public static Menu getInputAppointmentHourMenu() {
        OptionMenu menu = new OptionMenu("Choose an Hour", null);
        return menu
            .setOptionGenerator(() -> OptionGeneratorCollection.getInputHourOptionGenerator(menu.getFormData()))
            .shouldAddMainMenuOption();
    }

     // The following are shared by Pharmacists and Admins
    public static Menu getViewInventoryMenu() {
        OptionMenu menu = new OptionMenu("All Medications", "")
        .shouldAddLogoutOptions()
        .shouldAddMainMenuOption()
        .setOptionGenerator(() -> OptionGeneratorCollection.generateMedicationOptions(Control.NONE));
        return menu;
    }
}
