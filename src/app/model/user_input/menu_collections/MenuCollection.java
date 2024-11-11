package app.model.user_input.menu_collections;

import app.constants.AppMetadata;
import app.model.user_input.InputMenu;
import app.model.user_input.MenuState;
import app.model.user_input.Menu;
import app.model.user_input.OptionMenu;
import app.model.user_input.option_collections.OptionGeneratorCollection;
import app.model.user_input.option_collections.OptionGeneratorCollection.Control;
import app.model.users.Patient;
import app.service.MenuService;
import app.service.UserService;
import java.util.HashMap;
import java.util.Map;

public class MenuCollection {
    
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
        InputMenu menu = new InputMenu("Login Password Menu", "Please enter your password")
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
                    MenuService.setCurrentMenu(MenuState.LOGIN_USERNAME);
                    System.out.println((String) formData.get("username"));
                    System.out.println((String) formData.get("input"));
                    return null;
                }
            });
        return menu;
    }

    public static Menu getViewInventoryMenu() {
        OptionMenu menu = new OptionMenu("All Medications", "")
        .shouldAddLogoutOptions()
        .shouldAddMainMenuOption();

        setOptionGeneratorForInventory(menu);
        return menu;
    }

    public static Menu setOptionGeneratorForInventory(OptionMenu menu) {
        menu.setOptionGenerator(() -> {
                Control ctl = Control.NONE;
                Map<String, Object> formValues = menu.getFormData();
                if (formValues != null && formValues.containsKey("ctl")) {
                    ctl = (Control) formValues.get("ctl");
                }
                return OptionGeneratorCollection.getMedicationDisplayOptions(ctl);
            });
        return menu;
    }
}
