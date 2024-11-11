package app.model.user_input;

import app.model.user_input.FunctionalInterfaces.DisplayGenerator;

public class InputMenu extends Menu {
    
    private final Input input;

    public InputMenu(String title, String label) {
        super(title, label);
        this.input = new Input();
    }

    public Input getInput() {
        return input;
    }

    @Override
    public Input getField (String userInput) {
        this.formData.put("input", userInput);
        return this.input;
    }

    @Override
    public void display() {
        if (!(this.title == null || this.title.length() < 1)) {
            System.out.println(this.title);
            Menu.printLineBreak(50);
        }

        if (!(this.label == null || this.label.length() < 1)) {
            System.out.print("\n" + this.label + " ");
        }
    }

    @Override
    public InputMenu setParseUserInput(Boolean parseUserInput) {
        super.setParseUserInput(parseUserInput);  // Call the superclass method
        return this;
    }

    @Override
    public InputMenu setDisplayGenerator(DisplayGenerator displayGenerator) {
        super.setDisplayGenerator(displayGenerator);
        return this;
    }
}