package app.model.user_input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import app.constants.AppMetadata;
import app.constants.exceptions.ExitApplication;
import app.model.users.Patient;
import app.service.MenuService;
import app.service.UserService;

/**
* Controls which menus to show (Equivalent to machine in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-24
*/


public enum Menu {

    // Init START
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
    )),
    PATIENT_MAIN_MENU(new MenuBuilder(
        MenuType.SELECT,
        "Patient Main Menu",
        ""
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
                    // TODO: set remaining user types' menus
                    if (UserService.getCurrentUser().getClass() == Patient.class) {
                        MenuService.getCurrentMenu().setNextMenu(Menu.PATIENT_MAIN_MENU);
                    } else {
                        throw new Exception("User type not found");
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage() + "\n");
                    MenuService.getCurrentMenu().setNextMenu(Menu.LOGIN_USERNAME);
                }
                return null;
            });
        Menu.PATIENT_MAIN_MENU.makeLogoutMenu();
    }
    // Init END

    // Helper START
    private enum MenuType {
        DISPLAY,
        INPUT,
        SELECT
    }

    public interface ThrowableFunction<T, U, R, E extends Exception> {
        R apply(T t, U u) throws E;
    }

    private interface NextAction extends ThrowableFunction<
        String,
        Map<String, Object>,
        Map<String, Object>,
        Exception
    > {}

    private enum DisplayMode {
        MATCH_FOUND,
        NO_MATCH_FOUND,
        MULTIPLE_MATCHES_FOUND,
        INITIAL
    }

    private class Option {
        private final String label;
        private final String matchPattern;
        private final boolean isNumberedOption;
        private Menu nextMenu;
        private NextAction nextAction;
    
        public Option(
            String label,
            String matchPattern,
            boolean isNumberedOption
        ) {
            this.label = label;
            this.matchPattern = matchPattern;
            this.isNumberedOption = isNumberedOption;
        };
    
        private Option setNextMenu(Menu nextMenu) {
            this.nextMenu = nextMenu;
            return this;
        }
    
        private Option setNextAction(NextAction nextAction) {
            this.nextAction = nextAction;
            return this;
        }
    }
    // Helper END

    // Builder START
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
    // Builder END

    private final String title;
    private final String label;
    private final MenuType menuType;
    // Transitions & Actions START
    private Menu nextMenu;
    private NextAction nextAction;
    private Map<String, Object> dataFromPreviousMenu;
    private String userInput;
    // Transitions & Actions END
    // Options START
    private List<Option> options;
    private List<Option> matchingOptions;
    private DisplayMode displayMode = DisplayMode.INITIAL;
    // Options END
        
    Menu(MenuBuilder menuBuilder) {
        this.menuType = menuBuilder.menuType;
        this.title = menuBuilder.title;
        this.label = menuBuilder.label;
    }

    public void display() {
        if (this.menuType == MenuType.SELECT && (this.options == null || this.options.size() < 1)) {
            throw new Error("Menu with type select shold have at least one option");
        }

        if (!(this.title == null || this.title.length() < 1)) {
            System.out.println(this.title);
            IntStream.range(0, 30).forEach(n -> System.out.print("-"));
        }

        if (!(this.label == null || this.label.length() < 1)) {
            System.out.print("\n" + this.label + (
                this.menuType == MenuType.INPUT ? " " : "\n"
            ));
        } else if (this.menuType == MenuType.SELECT) {
            System.out.println("");
            switch (this.displayMode) {
                case NO_MATCH_FOUND -> System.out.println("No option matched your selection. Please try again:");
                case MULTIPLE_MATCHES_FOUND -> System.out.println("Please be more specific:");
                case INITIAL -> System.out.println("Please select an option:");
                default -> { break; }
            }
            System.out.println("");
        }

        if (this.menuType == MenuType.SELECT) {

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
    }

    // Next state (transition + action) handling START
    public Menu handleUserInput(String userInput) throws Exception {
        if (!(this.menuType == MenuType.DISPLAY || userInput.length() > 0)) {
            throw new Exception("Please type something in:");
        }

        if (this.menuType == MenuType.SELECT) {
            this.matchingOptions = (ArrayList<Option>) this.getFilteredOptions(userInput, true);
            List<Option> unNumberedMatches = this.getFilteredOptions(userInput, false);
            int numberOfMatches = unNumberedMatches.size() + this.matchingOptions.size();
            Option option = null;
            if (numberOfMatches < 1) {
                this.displayMode = DisplayMode.NO_MATCH_FOUND;
            } else if (numberOfMatches > 1) {
                this.displayMode = DisplayMode.MULTIPLE_MATCHES_FOUND;
            } else {
                try {
                    unNumberedMatches.addAll(this.matchingOptions);
                    option = unNumberedMatches.get(0);
                } catch (Exception e) {
                    this.setNextMenu(this);
                    throw e;
                } catch (Error e) {
                    System.out.println("Something went wrong. Please contact your administrator and try again.");
                    System.out.println("Exiting application...");
                    throw new ExitApplication();
                }
            }

            this.setNextMenu(option == null ? this : option.nextMenu);
            this.setNextAction(option == null ? (a, b) -> null : (option.nextAction));
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
    // Next state (transition + action) handling END

    // Options handling START
    // Options handling - builder START
    private Menu addOption(Option option) {
        return this.addOptions(List.of(option));
    }

    private Menu addOptions(List<Option> options) {
        if (this.menuType != MenuType.SELECT) {
            throw new Error("Cannot add option to menu type select.");
        }

        if (this.options == null) {
            this.options = new ArrayList<>();
        }

        this.options.addAll(options);
        return this;
    }

    private Menu addExitOption() {
        return this.addOption(new Option(
            "Exit Application (E)",
            "^E$|exit(( )?((app)?plication)?)?(( )?\\(E\\))?",
            false
        ).setNextAction((input, args) -> { throw new ExitApplication(); }));
    }

    private Menu addLogoutOption() {
        return this.addOption(new Option(
            "Logout (LO)",
            "^LO$|log( )?out(( )?\\(LO\\))?",
            false
        ).setNextAction((input, args) -> {
            UserService.logout();
            MenuService.getCurrentMenu().displayMode = DisplayMode.INITIAL;
            MenuService.getCurrentMenu().setNextMenu(Menu.LOGIN_USERNAME);
            return null;
        }));
    }

    private Menu makeLogoutMenu() {
        this.addLogoutOption().addExitOption();
        return this;
    }
    // Options handling - builder END

    // Options handling - display & matching user input START
    private List<Option> getNumberedOptions(boolean getNumbered) {
        return IntStream.range(0, this.options.size())
            .filter(optionsIndex -> getNumbered == this.options.get(optionsIndex).isNumberedOption)
            .mapToObj((int optionsIndex) -> this.options.get(optionsIndex))
            .collect(Collectors.toCollection(ArrayList::new));
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
                            String.format("^%d", optionsIndex+1),
                            option.matchPattern,
                            String.format("%d[ ]+(\\.)?%s",
                                optionsIndex+1,
                                option.matchPattern
                            )
                        ) : option.matchPattern,
                    Pattern.CASE_INSENSITIVE
                );
                Matcher matcher = matchPattern.matcher(userInput);
                return matcher.find() ? option : null;
            }).filter(Objects::nonNull)
            .collect(Collectors.toCollection(ArrayList::new));
    }
    // Options handling - display & matching user input END
    // Options handling END
}
