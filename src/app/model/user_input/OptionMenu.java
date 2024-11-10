package app.model.user_input;

import app.model.user_input.FunctionalInterfaces.DisplayGenerator;
import app.model.user_input.FunctionalInterfaces.OptionGenerator;
import app.model.user_input.option_collections.OptionGeneratorCollection;
import java.util.ArrayList;
import java.util.List;

public class OptionMenu extends NewMenu {

    private enum DisplayMode {
        MATCH_FOUND,
        NO_MATCH_FOUND,
        MULTIPLE_MATCHES_FOUND,
        INITIAL
    }

    // Options START
    private List<Option> options;
    private List<Option> matchingOptions;
    private OptionGenerator optionGenerator;
    private DisplayMode displayMode = DisplayMode.INITIAL;
    private boolean shouldHaveMainMenuOption;
    private boolean shouldHaveLogoutOption;
    private OptionTable optionTable;
    // Options END

    public OptionMenu (String title, String label) {
        super(title, label);
    }

    public OptionGenerator getOptionGenerator() {
        return this.optionGenerator;
    }

    public OptionMenu setOptionGenerator(OptionGenerator optionGenerator) {
        this.optionGenerator = optionGenerator;
        return this;
    }

    @Override
    public Input getField (String userInput) {
        List<Option> matches = this.optionTable.getFilteredOptions(userInput);

        int totalMatches = matches.size();
        
        if (totalMatches < 1) {
            this.displayMode = DisplayMode.NO_MATCH_FOUND;
            System.out.println("No option match found"); // TODO: REMOVE
            throw new IllegalArgumentException("No match found for the selected input.");
        } else if (totalMatches > 1) {
            this.displayMode = DisplayMode.MULTIPLE_MATCHES_FOUND;
            return null;
        } else {
            return matches.get(0);
        }
    }

    public void display() {
        try {
            this.options = optionGenerator.apply();
            System.out.println("GETTING OPTIONS");
        } catch (Exception e) {
            System.out.println("No Options Found");
        }
        
        if (this.shouldHaveMainMenuOption) {
            this.addMainMenuOption();
        }

        if (this.shouldHaveLogoutOption) {
            this.addLogoutOptions();
        }
        System.out.println("Options table created");
        this.optionTable = new OptionTable(this.options);
        // else {
        //     // refresh options after editing
        //     this.matchingOptions = this.optionTable.getNumberedOptions(true);
        // } // TODO what is this for?

        if (this.options == null || this.options.size() < 1) {
            throw new Error("Menu with type select should have at least one option");
        }

        if (!(this.title == null || this.title.length() < 1)) {
            System.out.println(this.title);
            NewMenu.printLineBreak(50);
        }

        if (!(this.label == null || this.label.length() < 1)) {
            System.out.print("\n" + this.label + "\n\n");
        } else if (!this.optionTable.getNumberedOptions(true).isEmpty()) {
            System.out.println("");
            switch (this.displayMode) {
                case NO_MATCH_FOUND -> System.out.println("No option matched your selection. Please try again:");
                case MULTIPLE_MATCHES_FOUND -> System.out.println("Please be more specific:");
                case INITIAL -> System.out.println("Please select an option:");
                default -> { break; }
            }
            System.out.println("");
        }

        if (this.displayGenerator != null) {
            try {
                this.displayGenerator.apply();
            } catch (Exception e) {
                System.out.println("Error printing display");
            }
            
        }

        this.optionTable.printTable();
    }

    private NewMenu addOptions(List<Option> options) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }

        this.options.addAll(options); // append options

        return this;
    }

    private void addLogoutOptions() {
            this.addOptions(OptionGeneratorCollection.generateLogoutAndExitOptions());
    }

    public OptionMenu shouldAddLogoutOptions() {
        this.shouldHaveLogoutOption = true;
        return this;
    }

    private OptionMenu addMainMenuOption() {
        this.addOptions(OptionGeneratorCollection.generateMainMenuOption());
        return this;
    }

    public OptionMenu shouldAddMainMenuOption() {
        this.shouldHaveMainMenuOption = true;
        return this;
    }

    @Override
    public OptionMenu setParseUserInput(Boolean parseUserInput) {
        super.setParseUserInput(parseUserInput);  // Call the superclass method
        return this;
    }

    @Override
    public OptionMenu setDisplayGenerator(DisplayGenerator displayGenerator) {
        super.setDisplayGenerator(displayGenerator);
        return this;
    }
}
