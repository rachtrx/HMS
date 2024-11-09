package app.model.user_input;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import app.constants.exceptions.ExitApplication;
import app.model.user_input.FunctionalInterfaces.OptionGenerator;
import app.service.MenuService;
import app.service.UserService;

import java.util.Map;

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

    public void setOptionGenerator(OptionGenerator optionGenerator) {
        this.optionGenerator = optionGenerator;
    }

    @Override
    public Field getField (String userInput) {
        List<Option> numberedMatches = this.optionTable.getFilteredOptions(userInput, true);
        List<Option> unNumberedMatches = this.optionTable.getFilteredOptions(userInput, false);

        int totalMatches = numberedMatches.size() + unNumberedMatches.size();
        
        if (totalMatches < 1) {
            this.displayMode = DisplayMode.NO_MATCH_FOUND;
            System.out.println("No option match found"); // TODO: REMOVE
            return null;
        } else if (totalMatches > 1) {
            this.displayMode = DisplayMode.MULTIPLE_MATCHES_FOUND;
            return null;
        } else {
            List<Option> combinedMatches = new ArrayList<>();
            combinedMatches.addAll(numberedMatches);
            combinedMatches.addAll(unNumberedMatches);
            return combinedMatches.get(0);
        }
    }

    public void display() {
        if (this.optionGenerator != null) {
            this.options = optionGenerator.apply();
            if (
                this.options != null &&
                this.matchingOptions != null &&
                this.options.size() >= this.matchingOptions.size()
            ) {
                // refresh options after editing
                this.matchingOptions = this.getNumberedOptions(true);
            }
        }

        if (this.shouldHaveMainMenuOption) {
            this.addMainMenuOption();
        }

        if (this.shouldHaveLogoutOption) {
            this.addLogoutOptions();
        }

        if (this.options == null || this.options.size() < 1) {
            throw new Error("Menu with type select should have at least one option");
        }

        if (!(this.title == null || this.title.length() < 1)) {
            System.out.println(this.title);
            Menu.printLineBreak(50);
        }

        if (!(this.label == null || this.label.length() < 1)) {
            System.out.print("\n" + this.label + (
                this.menuType == MenuType.INPUT ? " " : "\n\n"
            ));
        } else if (!this.getNumberedOptions(true).isEmpty()) {
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
            this.displayGenerator.apply();
            if (this == Menu.CONFIRM) this.setDisplayGenerator(null);
        }

        // Display numbered options
        List<Option> matches = (
            this.matchingOptions == null || this.matchingOptions.size() < 1
            ) ? this.getNumberedOptions(true) : this.matchingOptions;
        IntStream.range(0, matches.size())
            .forEach(optionIndex -> System.out.println(String.format(
                "%d. %s",
                optionIndex + 1,
                matches.get(optionIndex).label
            )));

        System.out.println("");

        // Display un-numbered options
        this.getNumberedOptions(false).forEach(option -> System.out.println(option.label));

        System.out.println("");
    }

    private Menu addOption(Option option) {
        return this.addOptions(new ArrayList<>(List.of(option)));
    }

    private Menu addOptions(List<Option> options) {
        if (this.menuType != MenuType.SELECT) {
            throw new Error("Cannot add option to menu type select.");
        }

        if (this.options == null) {
            this.options = new ArrayList<>();
        }

        this.options.addAll(options); // append options

        return this;
    }

    private Menu setOptions(List<Option> options) {
        this.options = options;
        return this;
    }

    private boolean optionExists(String optionLabel) {
        return this.options != null &&
            this.options.stream().anyMatch(option -> optionLabel.equals(option.label));
    }

    private void addLogoutOptions() {
        String logoutLabel = "Logout (LO)";
        if (!this.optionExists(logoutLabel)) {
            this.addOption(new Option(
                    logoutLabel,
                    "^LO$|log( )?out(( )?\\(LO\\))?",
                    false
                ).setNextAction((input, args) -> {
                    UserService.logout();
                    MenuService.getCurrentMenu().displayMode = DisplayMode.INITIAL;
                    return null;
                }).setNextMenu(Menu.LOGIN_USERNAME)
            );
        }

        String exitLabel = "Exit Application (E)";
        if (!this.optionExists(exitLabel)) {
            this.addOption(new Option(
                exitLabel,
                "^E$|exit(( )?((app)?plication)?)?(( )?\\(E\\))?",
                false
            ).setNextAction((input, args) -> { throw new ExitApplication(); }));
        }
    }

    private NewMenu shouldAddLogoutOptions() {
        this.shouldHaveLogoutOption = true;
        return this;
    }

    public Menu handleUserInput() {
        // 
    }

    private Menu setOptionGenerator(OptionGenerator optionGenerator) {
        this.optionGenerator = optionGenerator;
        return this;
    }

    // Options handling - display & matching user input START
    private List<Option> getNumberedOptions(boolean getNumbered) {
        return IntStream.range(0, this.options.size())
            .filter(optionsIndex -> getNumbered == this.options.get(optionsIndex).isNumberedOption)
            .mapToObj((int optionsIndex) -> this.options.get(optionsIndex))
            .collect(Collectors.toList());
    }

    private List<Option> getFilteredOptions(String userInput, boolean numbered) {
        List<Option> filteredOptions = this.getNumberedOptions(numbered);
        return IntStream.range(0, filteredOptions.size())
            .mapToObj(optionsIndex -> {
                Option option = filteredOptions.get(optionsIndex);
                Pattern matchPattern = Pattern.compile(
                    numbered ? 
                        String.join(
                            "|",
                            String.format("^%d(\\.)?$", optionsIndex+1),
                            option.matchPattern,
                            String.format("%d[ ]+(\\.)?%s",
                                optionsIndex+1,
                                option.matchPattern
                            )
                        ) : option.matchPattern,
                    Pattern.CASE_INSENSITIVE
                );
                Matcher matcher = matchPattern.matcher(userInput);

                boolean optionFound = matcher.find();
                if(optionFound) {
                    System.out.println(option.label);
                    return option;
                }
                return null;
                // return matcher.find() ? option : null;
            }).filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private Menu addMainMenuOption() {
        // String backLabel = "Back (B)";
        String mainMenuLabel = "Main Menu (M)";
        if (!this.optionExists(mainMenuLabel)) {
            this.addOption(new Option(
                mainMenuLabel,
                    // "^B$|back(( )?\\(B\\))?",
                    "^M$|main|menu|(main|menu|main menu)(( )?\\(M\\))?",
                    false
                ).setNextMenu(() -> Menu.getUserMainMenu())
            );
        }
        return this;
    }

    private Menu shouldAddMainMenuOption() {
        this.shouldHaveMainMenuOption = true;
        return this;
    }
}
