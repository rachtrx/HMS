package app.model.user_input.menu_collections;

import java.util.HashMap;
import java.util.Map;

import app.model.user_input.InputMenu;
import app.model.user_input.MenuState;
import app.model.user_input.OptionGeneratorCollection;
import app.model.user_input.OptionMenu;
import app.model.user_input.NewMenu;
import app.service.UserService;

public class MenuCollection {
    
    public static NewMenu getEditMenu() {
        return new InputMenu("Edit Menu", "Enter a new value: ");
    }

    public static NewMenu getConfirmMenu() {
        return new OptionMenu("Confirm Menu", "Please confirm your decision: ");
    }

    public static NewMenu getLandingMenu() {
        InputMenu menu = InputMenu("Landing Menu", "Press any key to continue")
            .getInput()
            .setNextAction((formData) -> new HashMap<>())
            .setNextMenuState(MenuState.LOGIN_USERNAME)
            .setExitMenuState(MenuState.LANDING);

        return menu;
    }

    public static NewMenu getLoginUsernameMenu() {
        InputMenu menu = new InputMenu("Login Username Menu", "Please enter your username") 
            .setNextMenuState(MenuState.LOGIN_PASSWORD)
            .setNextAction((formData) -> new HashMap<String, Object>() {{
                put("username", menu.getInput().getValue());
            }});
        return menu;
    }

    public static NewMenu getLoginPasswordMenu() {
        InputMenu menu = new InputMenu("Login Password Menu", "Please enter your password")
            .setParseUserInput(false)
            .getInput()
            .setNextAction((formData) -> {
                UserService.login((String) menu.getFormData().get("username"), menu.getInput().getValue());
                return null;
            })
            .setExitMenuState(MenuState.LOGIN_USERNAME);
        return menu;
    }
}
