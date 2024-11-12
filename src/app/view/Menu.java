package app.view;

import app.constants.exceptions.ExitApplication;
import app.constants.exceptions.InvalidCharacterException;
import app.constants.exceptions.InvalidLengthException;
import app.constants.exceptions.InvalidPhoneNumberException;
import app.constants.exceptions.InvalidTimeslotException;
import app.constants.exceptions.MissingCharacterException;
import app.constants.exceptions.NonNegativeException;
import app.controller.MenuService;
import app.utils.LoggerUtils;
import app.view.FunctionalInterfaces.DisplayGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;


public abstract class Menu {
    protected String title;
    protected String label;
    public void setLabel(String label) {
        this.label = label;
    }

    // Transitions & Actions START
    protected DisplayGenerator displayGenerator;
    protected Map<String, Object> formData = new HashMap<>();
    protected Boolean parseUserInput = true;
    protected Boolean changed = true;

    protected MenuState menuState;

    private Menu nextMenu;
    private Menu previousMenu;

    public abstract Input getField(String userInput);
    public abstract void display();
    
    // Transitions & Actions END

    public Menu(String title, String label) {
        this.title = title;
        this.label = label;
    }

    public MenuState getMenuState() {
        return menuState;
    }

    public Menu setMenuState(MenuState menuState) {
        this.menuState = menuState;
        return this;
    }

    public Map<String, Object> getFormData() {
        return formData;
    }

    public Menu setFormData(Map<String, Object> formData) {
        // shallow copy 
        if (formData == null) formData = new HashMap<>();
        this.formData = new HashMap<>(formData);
        return this;
    }

    public static void printLineBreak(int length) {
        IntStream.range(0, length).forEach(n -> System.out.print("-"));
        System.out.println();
    }

    public Menu setDisplayGenerator(DisplayGenerator displayGenerator) {
        this.displayGenerator = displayGenerator;
        return this;
    }

    public Menu setParseUserInput(Boolean parseUserInput) {
        this.parseUserInput = parseUserInput;
        return this;
    }

    public MenuState handleUserInput(String userInput) throws Exception {

        if (this.parseUserInput) {
            userInput = userInput.trim().toLowerCase();
        }
        
        LoggerUtils.info("Before Executing Action: " + this.menuState);

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
            } else {
                throw new IllegalArgumentException("Field is null!");
            }

            MenuState nextMenuState = field.getNextMenuState(); // IMPT only set data to null afterwards

            if (field.nextAction != null) {
                LoggerUtils.info("Calling form values: " + this.formData);
                this.formData = (Map<String, Object>) field.nextAction.apply(this.formData);
                LoggerUtils.info("Returned form values: " + this.formData); // TODO: remove test
                LoggerUtils.info("After Executing Action: " + nextMenuState);
            }
            
            return nextMenuState;
        } catch (IllegalArgumentException | InvalidCharacterException | InvalidTimeslotException | NonNegativeException | MissingCharacterException | InvalidPhoneNumberException | InvalidLengthException e) {
            // e.printStackTrace();
            System.out.println(e.getMessage());
            if(field != null && field.getExitMenuState() != null) return field.getExitMenuState();
            if(this.getMenuState() == MenuState.EDIT || this.getMenuState() == MenuState.CONFIRM) return this.getPreviousMenu().getMenuState();
            return this.getMenuState();
            // return MenuState.getUserMainMenuState();
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Something went wrong. Please contact your administrator and try again.");
            System.out.println("Exiting application...");
            throw new ExitApplication();
        }
    }

    public Menu getNextMenu() {
        return nextMenu;
    }

    public void setNextMenu(Menu nextMenu) {
        this.nextMenu = nextMenu;
    }

    public Menu getPreviousMenu() {
        return previousMenu;
    }

    public Menu setPreviousMenu(Menu previousMenu) {
        this.previousMenu = previousMenu;
        return this;
    }

    public Boolean isChanged() {
        return changed == true;
    }

    public void setChanged(Boolean changed) {
        this.changed = changed;
    }
}
