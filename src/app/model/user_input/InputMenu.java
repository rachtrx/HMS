package app.model.user_input;

import app.model.user_input.FunctionalInterfaces.NextAction;

import java.util.Map;

import MenuUtils.MenuType;

public class InputMenu extends NewMenu {
    
    private final Input input;

    public InputMenu(String title, String label) {
        super(title, label);
        this.input = new Input();
    }

    public Input getInput() {
        return input;
    }

    @Override
    public Field getField (String userInput) {
        this.input.setValue(userInput);
        return this.input;
    }

    public void display() {
        if (!(this.title == null || this.title.length() < 1)) {
            System.out.println(this.title);
            NewMenu.printLineBreak(50);
        }

        if (!(this.label == null || this.label.length() < 1)) {
            System.out.print("\n" + this.label + " ");
        }
    }
}
