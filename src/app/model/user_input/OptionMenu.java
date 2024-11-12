package app.model.user_input;

import app.db.DatabaseManager;
import app.model.user_input.FunctionalInterfaces.DisplayGenerator;
import app.model.user_input.FunctionalInterfaces.OptionGenerator;
import app.model.user_input.option_collections.OptionGeneratorCollection;
import app.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;

public class OptionMenu extends Menu {

    // Options START
    private List<Option> options = new ArrayList<>();
    private OptionGenerator optionGenerator;
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
        
        if (totalMatches < 1 || totalMatches > 1) {
            System.out.println("No option match found"); // TODO: REMOVE
            throw new IllegalArgumentException("No match found for the selected input.");
        } else {
            return matches.get(0);
        }
    }

    public void display() {
        this.options = new ArrayList<>();

        try {
            this.options = optionGenerator.apply();
            LoggerUtils.info("GETTING OPTIONS");
        } catch (Exception e) {
            System.out.println("No Options Found");
        }

        boolean optionsFound = !this.options.isEmpty();
        
        if (!optionsFound || this.shouldHaveMainMenuOption) {
            this.addMainMenuOption();
        }

        if (this.shouldHaveLogoutOption) {
            this.addLogoutOptions();
        }
        LoggerUtils.info("Options table created");
        this.optionTable = new OptionTable(this.options);

        // if (this.options == null || this.options.size() < 1) { // IMPT now always add main menu when no options
        //     throw new Error("Menu with type select should have at least one option");
        // }

        if (!(this.title == null || this.title.length() < 1)) {
            System.out.println(this.title);
            Menu.printLineBreak(50);
        }

        if (!(this.label == null || this.label.length() < 1)) {
            System.out.print("\n" + this.label + "\n");
        } else if (!this.optionTable.getNumberedOptions(true).isEmpty()) {
            System.out.println("Please select an option:");
            // switch (this.displayMode) {
            //     case NO_MATCH_FOUND -> System.out.println("No option matched your selection. Please try again:");
            //     case MULTIPLE_MATCHES_FOUND -> System.out.println("Please be more specific:");
            //     case INITIAL -> System.out.println("Please select an option:");
            //     default -> { break; }
            // }
            // System.out.println("");
        }

        if (this.displayGenerator != null) {
            try {
                this.displayGenerator.apply();
                System.out.println("\n");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error printing display");
            }
            
        }
        this.optionTable.printTable();

        if (!this.menuState.isResetMenu() && this.menuState != MenuState.CONFIRM && !this.changed) {
            System.err.println("(or enter '\\q' to go back) ");
        }
    }

    private Menu addOptions(List<Option> options) {
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
