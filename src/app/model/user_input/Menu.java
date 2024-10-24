package app.model.user_input;

import app.constants.AppMetadata;
import app.service.MenuService;
import app.service.UserService;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
* Controls which menus to show (Equivalent to machine in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-17
*/


public enum Menu {

    LANDING(new MenuBuilder(
        MenuType.DISPLAY,
        null,
        String.join(
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
        )
    )),
    LOGIN_USERNAME(new MenuBuilder(
        MenuType.INPUT,
        "Login",
        "Please enter your username: "
    )),
    LOGIN_PASSWORD(new MenuBuilder(
        MenuType.INPUT,
        "Login",
        "Please enter your password: "
    ));

    // Transitions
    static {
        Menu.LANDING.setNextMenu(Menu.LOGIN_USERNAME);
        Menu.LOGIN_USERNAME
            .setNextMenu(Menu.LOGIN_PASSWORD)
            .setNextAction((userInput, args) -> new HashMap<String, Object>() {{
                put("username", userInput);
            }});
        Menu.LOGIN_PASSWORD
            .setNextAction((userInput, args) -> {
                try {
                    UserService.login((String) args.get("username"), userInput);
                    MenuService.getCurrentMenu().setNextMenu(Menu.LANDING);
                } catch (Exception e) {
                    System.out.println(e.getMessage() + "\n");
                    MenuService.getCurrentMenu().setNextMenu(Menu.LOGIN_USERNAME); // TODO: set next menus
                }
                return null;
            });
    }

    private enum MenuType {
        DISPLAY,
        INPUT,
        SELECT
    }

    public interface ThrowableBiFunction<T, U, R, E extends Exception> {
        R apply(T t, U u) throws E;
    }

    private interface NextAction extends ThrowableBiFunction<
        String,
        Map<String, Object>,
        Map<String, Object>,
        Exception
    > {}

    private final String title;
    private final String label;
    private final MenuType menuType;
    private Menu nextMenu;
    private NextAction nextAction;
    private Map<String, Object> dataFromPreviousMenu;
    private String userInput;

    private static class MenuBuilder {

        private final MenuType menuType;
        private final String title;
        private final String label;
        
        MenuBuilder(MenuType menuType, String title, String label) {
            this.menuType = menuType;
            this.title = title;
            this.label = label;
        }
    }
        
    Menu(MenuBuilder menuBuilder) {
        this.menuType = menuBuilder.menuType;
        this.title = menuBuilder.title;
        this.label = menuBuilder.label;
    }

    public void display() {
        if (this.title != null) {
            System.out.println(this.title);
            IntStream.range(0, 30).forEach(n -> System.out.print("-"));
        }
        if (this.label != null) {
            System.out.println("\n" + this.label);
        }
    }

    public Menu handleUserInput(String userInput) throws Exception {
        if (!(this.menuType == MenuType.DISPLAY || userInput.length() > 0)) {
            throw new Exception("Please type something in:");
        }

        Map<String, Object> argsForNext = this.setUserInput(userInput).executeNextAction();
        return this.nextMenu.setDataFromPreviousMenu(argsForNext);
    }

    private Menu setUserInput(String userInput) {
        this.userInput = userInput;
        return this;
    }

    private Menu setNextAction(NextAction nextAction) {
        this.nextAction = nextAction;
        return this;
    }

    private Map<String, Object> executeNextAction() throws Exception {
        if (this.nextAction == null) {
            return null;
        }
            
        return this.nextAction.apply(this.userInput, this.dataFromPreviousMenu);
    }

    private Menu setNextMenu(Menu nextMenu) {
        this.nextMenu = nextMenu;
        return this;
    }

    private Menu setDataFromPreviousMenu(Map<String, Object> dataFromPreviousMenu) {
        this.dataFromPreviousMenu = dataFromPreviousMenu;
        return this;
    }
}

/*
     * FLOW
     * current menu display
     * user input
     * current menu next --> get from list of menus in MenuService
     * 
     * menu list
     * transition list in MenuService
     * current menu points to next menu
     * options rendered if present in BaseMenu
     */
