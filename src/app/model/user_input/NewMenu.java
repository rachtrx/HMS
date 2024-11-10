package app.model.user_input;

import app.constants.exceptions.ExitApplication;
import app.model.user_input.FunctionalInterfaces.DisplayGenerator;
import app.service.MenuService;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;


public abstract class NewMenu {
    protected String title;
    protected String label;
    public void setLabel(String label) {
        this.label = label;
    }

    // Transitions & Actions START
    protected DisplayGenerator displayGenerator;
    protected Map<String, Object> formData = new HashMap<>();
    protected Boolean parseUserInput = true;
    protected MenuState menuState;

    private NewMenu nextMenu;
    private NewMenu previousMenu;

    public abstract Input getField(String userInput);
    public abstract void display();
    
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
        return this;
    }

    public Map<String, Object> getFormData() {
        return formData;
    }

    public NewMenu setFormData(Map<String, Object> formData) {
        // shallow copy 
        if (formData == null) formData = new HashMap<>();
        this.formData = new HashMap<>(formData);
        return this;
    }

    public static void printLineBreak(int length) {
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

    public MenuState handleUserInput(String userInput) throws Exception {

        if (this.parseUserInput) {
            userInput = userInput.trim().toLowerCase();
        }

        Input field = null;
        try {
            field = this.getField(userInput);
            if (field != null) {
                if (field.isRequiresConfirmation() || field.isEditRedirect()) {
                    this.formData.put("nextAction", field.getNextAction());
                    this.formData.put("nextState", field.getNextMenuState());
                    this.formData.put("exitState", field.getExitMenuState());
                    // this.setDataFromPreviousMenu(null);
                    return field.isRequiresConfirmation() ? 
                        MenuState.CONFIRM : 
                        MenuState.EDIT;
                }
            }

            System.out.printf("Before Executing Action: %s%n", this.menuState);
            
            if (field == null) throw new Exception("Next action not defined!");

            MenuState nextMenuState = field.getNextMenuState(); // IMPT only set data to null afterwards

            if (field.nextAction != null) {
                System.out.println("Calling form values: " + this.formData);
                this.formData = (Map<String, Object>) field.nextAction.apply(this.formData);
                System.out.println("Returned form values: " + this.formData); // TODO: remove test
                System.out.printf("After Executing Action: %s%n", nextMenuState);
            }
            
            return nextMenuState;
        } catch (IllegalArgumentException e) {
            System.out.println("Value not found, please enter a new value");
            if(field != null && field.getExitMenuState() != null) return field.getExitMenuState();
            return MenuState.getUserMainMenuState();
        } catch (Exception e) {
            e.printStackTrace();
            if (this.menuState.equals(MenuState.CONFIRM)) {
                MenuService.setCurrentMenu(
                    field == null || field.getExitMenuState() == null ?
                        MenuState.getUserMainMenuState() :
                        field.getExitMenuState()
                );
                throw e;
            }

            System.out.println("Something went wrong. Please contact your administrator and try again.");
            System.out.println("Exiting application...");
            throw new ExitApplication();
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
