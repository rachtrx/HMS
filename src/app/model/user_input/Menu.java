package app.model.user_input;

import app.constants.AppMetadata;
import app.constants.exceptions.ExitApplication;
import app.model.appointments.Appointment;
import app.model.appointments.Timeslot;
import app.model.users.Patient;
import app.model.users.staff.Doctor;
import app.service.AppointmentService;
import app.service.MenuService;
import app.service.UserService;
import app.utils.DateTimeUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
* Controls which menus to show (Equivalent to machine in FSM)
*
* @author Luke Eng (@LEPK02)
* @version 1.0
* @since 2024-10-24
*/


public enum Menu {

    // Init START
    EDIT(new MenuBuilder(
        MenuType.INPUT,
        null,
        "Enter a new value: "
    )),
    CONFIRM(new MenuBuilder(
        MenuType.SELECT,
        "Confirm Action? ",
        null
    )),
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
        null
    )),
    PATIENT_VIEW_MEDICAL_RECORD(new MenuBuilder(
        MenuType.SELECT,
        "Patient Medical Record",
        "Enter 'M' or 'Menu' to return to the main menu."
    )),
    PATIENT_EDIT_MEDICAL_RECORD(new MenuBuilder(
        MenuType.SELECT,
        "Patient Medical Record",
        "Please select a field to edit:"
    )),
    PATIENT_VIEW_AVAILABLE_APPOINTMENTS(new MenuBuilder(
        MenuType.SELECT,
        "Available Appointments Today",
        null
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
            .setParseUserInput(false)
            .setNextAction((userInput, args) -> {
                try {
                    UserService.login((String) args.get("username"), userInput);
                    MenuService.getCurrentMenu().setNextMenu(() -> Menu.getUserMainMenu());
                } catch (Exception e) {
                    System.out.println(e.getMessage() + "\n");
                    MenuService.getCurrentMenu().setNextMenu(Menu.LOGIN_USERNAME);
                }
                return null;
            });
        Menu.PATIENT_MAIN_MENU
            .setOptionGenerator(() -> new ArrayList<>(List.of(
                // TODO: complete remaining options
                new Option(
                        "View Medical Record", 
                        "(view( )?)?medical(( )?record)?", 
                        true
                    ).setNextMenu(() -> PATIENT_VIEW_MEDICAL_RECORD),
                new Option(
                        "Edit Contact Information", 
                        "(edit( )?)?contact(( )?info(rmation)?)?", 
                        true
                    ).setNextMenu(() -> PATIENT_EDIT_MEDICAL_RECORD),
                new Option(
                        "View Available Appointments Today", 
                        "view( )?(available( )?)?appointment(s)?|(view( )?)?today", 
                        true
                    ).setNextMenu(() -> PATIENT_VIEW_AVAILABLE_APPOINTMENTS)
            ))).shouldAddLogoutOptions();
        Menu.PATIENT_VIEW_MEDICAL_RECORD
            .setDisplayGenerator(() -> {
                Patient patient = (Patient) UserService.getCurrentUser();
                System.out.println();
                System.out.println("Patient Information");
                Menu.printLineBreak(10);
                System.out.println(String.format(
                    "1. Patient Name: %s", patient.getName()
                ));
                System.out.println(String.format(
                    "2. Patient ID: P%d", patient.getUserId()
                ));
                System.out.println(String.format(
                    "3. Date of Birth: %s", DateTimeUtil.printLongDate(patient.getDateOfBirth())
                ));
                System.out.println(String.format(
                    "4. Gender: %s", patient.getGender()
                ));
                System.out.println(String.format(
                    "5. Mobile Phone Number: +65%d", patient.getMobileNumber()
                ));
                System.out.println(String.format(
                    "6. Home Phone Number: +65%d", patient.getHomeNumber()
                ));
                System.out.println(String.format(
                    "7. Email: %s", patient.getEmail()
                ));
                System.out.println(String.format(
                    "8. Blood Type: %s", patient.getBloodType()
                ));

                System.out.println("\nAppointment History");
                Menu.printLineBreak(10);
                IntStream.range(0, patient.getAppointments().size())
                    .forEach(appointmentIndex -> {
                        Appointment appointment = patient.getAppointments().get(appointmentIndex);
                        System.out.println(String.format("%d.", appointmentIndex+1));
                        System.out.println(String.format(
                            "Appointment Timeslot: %s",
                            DateTimeUtil.printLongDateTime(appointment.getTimeslot())
                        ));
                        System.out.println(String.format(
                            "Patient Name: %s",
                            UserService
                                .findUserByIdAndType(appointment.getPatientId(), Patient.class)
                                .getName()
                        ));
                        Doctor doctor = UserService
                                .findUserByIdAndType(appointment.getDoctorId(), Doctor.class);
                        System.out.println(
                            doctor == null ?
                                "No doctor assigned." :
                                String.format("Doctor Name: %s", doctor.getName())
                        );
                        System.out.println(String.format(
                            "Appointment Status: %s",
                            appointment.getAppointmentStatus().toString()
                        ));
                        // TODO: display prescription
                    });
            })
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.PATIENT_EDIT_MEDICAL_RECORD
            .setOptionGenerator(() -> {
                Patient patient = (Patient) UserService.getCurrentUser();
                return new ArrayList<>(List.of(
                    new Option(
                            String.format("Mobile Number: +65%d", patient.getMobileNumber()),
                            "mobile(( )?number)?",
                            true
                        ).setNextMenuAsEdit((userInput, args) -> {
                            patient.setMobileNumber((String) args.get("confirm"));
                            return null;
                        }),
                    new Option(
                            String.format("Home Number: +65%d", patient.getHomeNumber()),
                            "home(( )?number)?",
                            true
                        ).setNextMenuAsEdit((userInput, args) -> {
                            patient.setHomeNumber((String) args.get("confirm"));
                            return null;
                        }),
                    new Option(
                            String.format("Email: %s", patient.getEmail()),
                            "email",
                            true
                        ).setNextMenuAsEdit((userInput, args) -> {
                            patient.setEmail((String) args.get("confirm"));
                            return null;
                        })
                    // new Option(
                    //         String.format("Name: %s", UserService.getCurrentUser().getName()),
                    //         "name|" + UserService.getCurrentUser().getName(),
                    //         true
                    //     ).setNextMenuAsEdit((userInput, args) -> {
                    //         UserService.getCurrentUser().setName((String) args.get("confirm"));
                    //         return null;
                    //     })
                ));
            }).shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
            Menu.PATIENT_VIEW_AVAILABLE_APPOINTMENTS
                .setOptionGenerator(() -> {
                    List<Timeslot> timeslots = AppointmentService.getAvailableAppointmentSlotsToday();
                    if (timeslots == null) {
                        System.out.println("No available timeslots today.");
                        return null;
                    }
                    return timeslots.stream()
                        .map(timeslot -> new Option(
                                DateTimeUtil.printLongDateTime(timeslot.getTimeSlot()),
                                DateTimeUtil.printShortDate(timeslot.getTimeSlot().toLocalDate()),
                                true
                            ) // TODO: set schedule appointment menu
                        ).collect(Collectors.toList());
                })
                .shouldAddMainMenuOption()
                .shouldAddLogoutOptions();
    }
    // Init END

    // Helper START
    private enum MenuType {
        DISPLAY,
        INPUT,
        SELECT
    }

    public interface DisplayGenerator {
        void apply() throws Exception;
    }

    public interface ThrowableBlankFunction<R, E extends Exception> {
        R apply() throws E;
    }

    private interface OptionGenerator extends ThrowableBlankFunction<List<Option>, Exception> {}
    private interface MenuGenerator extends ThrowableBlankFunction<Menu, Exception> {}

    public interface ThrowableBiFunction<T, U, R, E extends Exception> {
        R apply(T t, U u) throws E;
    }

    private interface NextAction extends ThrowableBiFunction<
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

    private static class Option {
        private final String label;
        private final String matchPattern;
        private final boolean isNumberedOption;
        private MenuGenerator nextMenuGenerator;
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

        private MenuGenerator getNextMenuGenerator() {
            return this.nextMenuGenerator;
        }
    
        private Option setNextMenu(Menu nextMenu) {
            return this.setNextMenu(() -> nextMenu);
        }

        private Option setNextMenu(MenuGenerator nextMenuGenerator) {
            this.nextMenuGenerator = nextMenuGenerator;
            return this;
        }

        private Option setNextMenuAsEdit(NextAction nextAction) {
            Menu currentMenu = MenuService.getCurrentMenu();
            return this.setNextMenu(() -> Menu.EDIT
                .setNextAction((userInput, args) -> {
                    return new HashMap<>(){{ put("confirm", userInput); }};
                })
                .setNextMenu(Menu.CONFIRM
                    .setOptions(new ArrayList<>(List.of(
                        new Option(
                                "Yes (Y)",
                                "yes|y|yes( )?\\(?y\\)?",
                                false
                            ).setNextAction((userInput, args) -> {
                                try {
                                    return nextAction.apply(null, args);
                                } catch (Exception e) {
                                    MenuService.setCurrentMenu(currentMenu);
                                    throw e;
                                }
                            })
                            .setNextMenu(MenuService.getCurrentMenu()),
                        new Option(
                            "No (N)",
                            "no|n|no( )?\\(?n\\)?",
                            false
                        ).setNextMenu(currentMenu)
                    )))
                )
            );
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
    private boolean parseUserInput = true;
    // Transitions & Actions START
    private MenuGenerator nextMenuGenerator;
    private NextAction nextAction;
    private Map<String, Object> dataFromPreviousMenu;
    private String userInput;
    // Transitions & Actions END
    // Options START
    private List<Option> options;
    private List<Option> matchingOptions;
    private OptionGenerator optionGenerator;
    private DisplayMode displayMode = DisplayMode.INITIAL;
    private DisplayGenerator displayGenerator;
    private boolean shouldHaveMainMenuOption;
    private boolean shouldHaveLogoutOption;
    // Options END
        
    Menu(MenuBuilder menuBuilder) {
        this.menuType = menuBuilder.menuType;
        this.title = menuBuilder.title;
        this.label = menuBuilder.label;
    }

    public Menu setDisplayGenerator(DisplayGenerator displayGenerator) {
        this.displayGenerator = displayGenerator;
        return this;

    }

    public void display() throws Exception {
        if (this.menuType == MenuType.SELECT) {
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
                throw new Error("Menu with type select shold have at least one option");
            }
        }

        if (!(this.title == null || this.title.length() < 1)) {
            System.out.println(this.title);
            Menu.printLineBreak(50);
        }

        if (!(this.label == null || this.label.length() < 1)) {
            System.out.print("\n" + this.label + (
                this.menuType == MenuType.INPUT ? " " : "\n"
            ));
        } else if (
            this.menuType == MenuType.SELECT &&
            !this.getNumberedOptions(true).isEmpty()
        ) {
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

    private static void printLineBreak(int length) {
        IntStream.range(0, length).forEach(n -> System.out.print("-"));
        System.out.println();
    }

    public Menu setParseUserInput(boolean parseUserInput) {
        this.parseUserInput = parseUserInput;
        return this;
    }

    // Next state (transition + action) handling START
    public Menu handleUserInput(String userInput) throws Exception {
        if (!(this.menuType == MenuType.DISPLAY || userInput.length() > 0)) {
            throw new Exception("Please type something in:");
        }

        if (this.parseUserInput) {
            userInput = userInput.trim().toLowerCase();
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

            this.setNextMenu(option == null ? () -> this : option.getNextMenuGenerator());
            this.setNextAction(option == null ? (a, b) -> null : (option.nextAction));
        }

        Map<String, Object> argsForNext = this.setUserInput(userInput).executeNextAction();
        return this.getNextMenu().setDataFromPreviousMenu(argsForNext);
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

    public Menu getNextMenu() throws Exception {
        return this.nextMenuGenerator.apply();
    }

    private Menu setNextMenu(Menu nextMenu) {
        return this.setNextMenu(() -> nextMenu);
    }

    private Menu setNextMenu(MenuGenerator nextMenuGenerator) {
        this.nextMenuGenerator = nextMenuGenerator;
        return this;
    }

    private Menu setDataFromPreviousMenu(Map<String, Object> dataFromPreviousMenu) {
        this.dataFromPreviousMenu = dataFromPreviousMenu;
        return this;
    }

    private static Menu getUserMainMenu() {
        try {
            // TODO: set remaining user types' menus
            if (Patient.class.equals(UserService.getCurrentUser().getClass())) {
                return Menu.PATIENT_MAIN_MENU;
            }
            throw new Exception();
        } catch (Exception e) {
            UserService.logout();
            return Menu.LOGIN_USERNAME;
        }

    }
    // Next state (transition + action) handling END

    // Options handling START
    // Options handling - builder START
    private Menu setOptionGenerator(OptionGenerator optionGenerator) {
        this.optionGenerator = optionGenerator;
        return this;
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

        this.options.addAll(options);

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

    private void addMainMenuOption() {
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
    }

    private Menu shouldAddMainMenuOption() {
        this.shouldHaveMainMenuOption = true;
        return this;
    }

    private Menu shouldAddLogoutOptions() {
        this.shouldHaveLogoutOption = true;
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
                            String.format("^%d$", optionsIndex+1),
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
