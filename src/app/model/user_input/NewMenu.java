package app.model.user_input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import app.constants.exceptions.ExitApplication;
import app.service.MenuService;

import app.model.user_input.FunctionalInterfaces.DisplayGenerator;


public abstract class NewMenu {
    protected String title;
    protected String label;
    // Transitions & Actions START
    protected DisplayGenerator displayGenerator;
    protected Map<String, Object> formData = new HashMap<>();
    protected Boolean parseUserInput;
    protected MenuState menuState;

    private NewMenu nextMenu;
    private NewMenu previousMenu;

    public abstract Field getField(String userInput);
    
    // Transitions & Actions END

    public NewMenu(String title, String label) {
        this.title = title;
        this.label = label;
    }

    public MenuState getMenuState() {
        return menuState;
    }

    public NewMenu setMenuState(MenuState menuState) {
        this.menuState = menuState;
    }

    public Map<String, Object> getFormData() {
        return formData;
    }

    public NewMenu setFormData(Map<String, Object> formData) {
        // shallow copy 
        this.formData = new HashMap<>(formData);
        return this;
    }

    protected static void printLineBreak(int length) {
        IntStream.range(0, length).forEach(n -> System.out.print("-"));
        System.out.println();
    }

    public NewMenu setDisplayGenerator(DisplayGenerator displayGenerator) {
        this.displayGenerator = displayGenerator;
        return this;
    }

    public NewMenu setParseUserInput(Boolean parseUserInput) {
        this.parseUserInput = parseUserInput;
        return this;
    }

    public NewMenu handleUserInput(String userInput) throws Exception {

        if (this.parseUserInput) {
            userInput = userInput.trim(); // Cannot lowercase since add user menus need true values, such as name, password etc 
        }

        Field field;
        try {
            field = this.getField(userInput);
            if (field != null) {
                if (field.isRequiresConfirmation() || field.isEditRedirect()) {
                    this.formData.put("nextAction", field.getNextAction());
                    this.formData.put("nextState", field.getNextMenuGenerator());
                    this.formData.put("exitState", field.getExitMenuGenerator());
                    // this.setDataFromPreviousMenu(null);
                    return field.isRequiresConfirmation() ? 
                        MenuState.CONFIRM.getMenu(this.formData) : 
                        MenuState.EDIT.getMenu(this.formData);
                }
            }

            System.out.printf("Before Executing Action: %s%n", this);
            
            if (field.nextAction == null) throw new Exception("Next action not defined!");
            this.formData = (Map<String, Object>) field.nextAction.apply(this.formData);
            System.out.println(this.formData); // TODO: remove test
            System.out.printf("After Executing Action: %s%n", this.getNextMenu());

            MenuState nextMenuState = field.getNextMenuState(); // IMPT only set data to null afterwards
            return nextMenuState.getMenu(this.formData);
        } catch (Exception e) {
            System.out.println("Something went wrong. Please contact your administrator and try again.");
            System.out.println("Exiting application...");
            throw new ExitApplication();

            if (this.equals(MenuState.CONFIRM)) {
                MenuService.setCurrentMenu(
                    field == null || field.getExitMenuState() == null ?
                        MenuState.getUserMainMenu() :
                        field.getExitMenuState().getMenu(this.formData)
                );
            }
            throw e;
        }
    }

    public NewMenu getNextMenu() {
        return nextMenu;
    }

    public void setNextMenu(NewMenu nextMenu) {
        this.nextMenu = nextMenu;
    }

    public NewMenu getPreviousMenu() {
        return previousMenu;
    }

    public void setPreviousMenu(NewMenu previousMenu) {
        this.previousMenu = previousMenu;
    }
}
