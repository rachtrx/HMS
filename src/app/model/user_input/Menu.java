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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
    PATIENT_APPOINTMENT_SELECTION_TYPE(new MenuBuilder(
        MenuType.SELECT,
        "Choose Date",
        null
    )),
    PATIENT_VIEW_AVAILABLE_APPOINTMENTS(new MenuBuilder(
        MenuType.SELECT,
        "Available Appointments Today",
        null
    )),
    INPUT_APPOINTMENT_YEAR(new MenuBuilder(
        MenuType.INPUT,
        "Choose Appointment Date",
        "Please enter the appointment year: "
    )),
    INPUT_APPOINTMENT_MONTH(new MenuBuilder(
        MenuType.INPUT,
        "Choose Appointment Date",
        "Please enter the appointment month as a number (1 = Jan, 12 = Dec): "
    )),
    INPUT_APPOINTMENT_DAY(new MenuBuilder(
        MenuType.INPUT,
        "Choose Appointment Date",
        "Please enter the appointment day: "
    )),
    INPUT_APPOINTMENT_HOUR(new MenuBuilder(
        MenuType.INPUT,
        "Choose Appointment Date",
        String.format(
            "Please enter the appointment start hour in 24H format (appointments last 1 hour between %d and %d): ",
            Timeslot.firstSlotStartTime.getHour(),
            Timeslot.lastSlotStartTime.getHour()
        )
    )),
    INPUT_APPOINTMENT_DOCTOR(new MenuBuilder(
        MenuType.SELECT,
        "Choose Appointment Doctor",
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
                    MenuService.getCurrentMenu().setNextMenu(() -> Menu.getUserMainMenu()); // pass in menugenerator
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
                    .setNextAction((a, b) -> new HashMap<String, Object>() {{
                        put("dateTime", LocalDateTime.now());
                    }}),
                new Option(
                        "Schedule an Appointment", 
                        "^schedule( )?(a(n)?( )?)?appointment(s)?",
                        true
                    ).setNextMenu(() -> PATIENT_APPOINTMENT_SELECTION_TYPE)
                    .setNextAction((a,b) -> new HashMap<String, Object>() {{
                            put("yearValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                            put("monthValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                            put("dayValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                            put("hourValidator", DateTimeUtil.DateConditions.FUTURE.toString());
                        }}
                    )
            ))).shouldAddLogoutOptions();
        Menu.PATIENT_VIEW_MEDICAL_RECORD
            .setDisplayGenerator(() -> {
                Patient patient = (Patient) UserService.getCurrentUser();
                System.out.println();
                System.out.println("Patient Information");
                Menu.printLineBreak(10);
                patient.print();

                System.out.println("\nAppointment History");
                Menu.printLineBreak(10);
                List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId());
                IntStream.range(0, appointments.size()) 
                    .forEach(appointmentIndex -> {
                        Appointment appointment = appointments.get(appointmentIndex);
                        System.out.println(String.format("%d.", appointmentIndex+1));
                        System.out.println(String.format(
                            "Appointment Timeslot: %s",
                            DateTimeUtil.printLongDateTime(appointment.getTimeslot())
                        ));
                        System.out.println(String.format(
                            "Patient Name: %s",
                            UserService
                                .findUserByIdAndType(appointment.getPatientId(), Patient.class, true) // TODO why is this needed though
                                .getName()
                        ));
                        Doctor doctor = UserService
                                .findUserByIdAndType(appointment.getDoctorId(), Doctor.class, true);
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
                if (
                    MenuService.getCurrentMenu().dataFromPreviousMenu != null &&
                    MenuService.getCurrentMenu().dataFromPreviousMenu.containsKey("dateTime")
                ) {
                    List<Timeslot> timeslots = AppointmentService.getAvailableAppointmentSlot((LocalDateTime) MenuService.getCurrentMenu().dataFromPreviousMenu.get("dateTime"));
                    if (timeslots != null) {
                        List<Option> options = timeslots.stream()
                            .map(timeslot -> new Option(
                                    DateTimeUtil.printLongDateTime(timeslot.getTimeSlot()),
                                    DateTimeUtil.printShortDate(timeslot.getTimeSlot().toLocalDate()),
                                    true
                                ).setNextMenu(INPUT_APPOINTMENT_DOCTOR)
                                .setNextAction((userInput, args) -> new HashMap<>(){{
                                    put("date", DateTimeUtil.printLongDateTime(timeslot.getTimeSlot()));
                                }})
                            ).collect(Collectors.toList());
                        if (!options.isEmpty()) {
                            return options;
                        }
                    }
                    System.out.println(String.format(
                        "No available timeslots today. Try again tomorrow before %02d:00.\n",
                        Timeslot.lastSlotStartTime.getHour()
                    ));
                }
                return null;
            })
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.PATIENT_APPOINTMENT_SELECTION_TYPE
            .setOptionGenerator(() -> {
                return new ArrayList<>(Arrays.asList(
                    new Option("Today", "today", true)
                        .setNextAction((userInput, args) -> new HashMap<String, Object>() {{
                            LocalDate today = LocalDate.now();
                            put("year", String.valueOf(today.getYear()));
                            put("month", String.valueOf(today.getMonthValue()));
                            put("day", String.valueOf(today.getDayOfMonth()));
                        }}).setNextMenu(Menu.INPUT_APPOINTMENT_HOUR), 
                    new Option("Tomorrow", "tmr|tomorrow", true)
                        .setNextAction((userInput, args) -> new HashMap<String, Object>() {{
                            LocalDate tmr = LocalDate.now().plusDays(1);
                            put("year", String.valueOf(tmr.getYear()));
                            put("month", String.valueOf(tmr.getMonthValue()));
                            put("day", String.valueOf(tmr.getDayOfMonth()));
                        }}).setNextMenu(Menu.INPUT_APPOINTMENT_HOUR), 
                    new Option("Custom", "custom", true)
                    .setNextAction((userInput, args) -> args).setNextMenu(Menu.INPUT_APPOINTMENT_YEAR)
                ));
            });
        Menu.INPUT_APPOINTMENT_YEAR
            .setNextMenu(Menu.INPUT_APPOINTMENT_MONTH)
            .setNextAction((userInput, args) -> {
                if (args.containsKey("yearValidator")) {
                    int userIntInput = Menu.parseUserIntInput(userInput);
                    if (
                        userIntInput > LocalDate.now().getYear() + 1 ||
                        userIntInput < LocalDate.now().getYear() - 1
                    ) {
                        throw new Exception(String.format(
                            "Please enter a number between %d to %d",
                            LocalDate.now().getYear() - 1,
                            LocalDate.now().getYear() + 1
                        ));
                    }
                    LocalDateTime originalDate = LocalDateTime.now();
                    DateTimeUtil.validateUserDateInput(
                        originalDate,
                        originalDate.withYear(userIntInput),
                        (String) args.get("yearValidator")
                    );
                }

                if (args.isEmpty()) {
                    args = new HashMap<>();
                }
                args.put("year", userInput);
                return args;
            });
        Menu.INPUT_APPOINTMENT_MONTH
            .setNextMenu(Menu.INPUT_APPOINTMENT_DAY)
            .setNextAction((userInput, args) -> {
                if (args.containsKey("monthValidator")) {
                    int userIntInput = Menu.parseUserIntInput(userInput);
                    if (userIntInput < 1 || userIntInput > 12) {
                        throw new Exception("Please enter a number between 1 (Jan) to 12 (Dec)");
                    }
                    LocalDateTime originalDate = LocalDateTime.now();
                    DateTimeUtil.validateUserDateInput(
                        originalDate,
                        originalDate
                            .withYear(Menu.parseUserIntInput((String) args.get("year")))
                            .withMonth(userIntInput),
                        (String) args.get("monthValidator")
                    );
                }

                args.put("month", userInput);
                return args;
            });
        Menu.INPUT_APPOINTMENT_DAY
            .setNextMenu(Menu.INPUT_APPOINTMENT_HOUR)
            .setNextAction((userInput, args) -> {
                if (args.containsKey("dayValidator")) {
                    int userIntInput = Menu.parseUserIntInput(userInput);
                    LocalDateTime originalDate = LocalDateTime.now();
                    LocalDateTime offsetDate = originalDate
                        .withYear(Menu.parseUserIntInput((String) args.get("year")))
                        .withMonth(Menu.parseUserIntInput((String) args.get("month")))
                        .withDayOfMonth(userIntInput);
                    DateTimeUtil.validateUserDateInput(
                        originalDate,
                        offsetDate,
                        (String) args.get("dayValidator")
                    );
                    if (
                        !(offsetDate.toLocalDate().isAfter(originalDate.toLocalDate())) &&
                        originalDate.getHour() > Timeslot.lastSlotStartTime.getHour()
                    ) {
                        throw new Exception("No appointments left for today. Please try another date.");
                    }
                }

                args.put("day", userInput);
                return args;
            });
        Menu.INPUT_APPOINTMENT_HOUR
            .setNextAction((userInput, args) -> {
                if (args.containsKey("hourValidator")) {
                    int userIntInput = Menu.parseUserIntInput(userInput);
                    LocalDateTime originalDate = LocalDateTime.now();
                    if (
                        userIntInput > Timeslot.lastSlotStartTime.getHour()  ||
                        userIntInput < Timeslot.firstSlotStartTime.getHour()
                    ) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
                        throw new Exception(String.format(
                            "Please enter a time during office hours (%sH - %sH)",
                            Timeslot.firstSlotStartTime.format(formatter),
                            Timeslot.lastSlotStartTime.format(formatter)
                        ));
                    }
                    DateTimeUtil.validateUserDateInput(
                        originalDate,
                        originalDate
                            .withYear(Menu.parseUserIntInput((String) args.get("year")))
                            .withMonth(Menu.parseUserIntInput((String) args.get("month")))
                            .withDayOfMonth(Menu.parseUserIntInput((String) args.get("day")))
                            .withHour(userIntInput),
                        (String) args.get("hourValidator")
                    );
                }

                return new HashMap<String, Object>(){{
                    put(
                        "dateTime", LocalDateTime.of(
                            Menu.parseUserIntInput((String) args.get("year")),
                            Menu.parseUserIntInput((String) args.get("month")),
                            Menu.parseUserIntInput((String) args.get("day")),
                            Menu.parseUserIntInput(userInput),
                            0
                        ));
                    put(
                        "date", 
                        DateTimeUtil.printLongDateTime(
                            LocalDateTime.of(
                                Menu.parseUserIntInput((String) args.get("year")),
                                Menu.parseUserIntInput((String) args.get("month")),
                                Menu.parseUserIntInput((String) args.get("day")),
                                Menu.parseUserIntInput(userInput),
                                0
                            )
                        )
                    );
                }};
            }).setNextMenu(Menu.INPUT_APPOINTMENT_DOCTOR);
        Menu.INPUT_APPOINTMENT_DOCTOR
            .setOptionGenerator(() -> {
                if (
                    MenuService.getCurrentMenu().dataFromPreviousMenu != null &&
                    MenuService.getCurrentMenu().dataFromPreviousMenu.containsKey("date")
                ) {
                    String selectedDateTimeString = 
                        (String) MenuService.getCurrentMenu().dataFromPreviousMenu.get("date");
                    LocalDateTime selectedDateTime = DateTimeUtil.parseLongDateTime(selectedDateTimeString);
                    List<Doctor> availableDoctors = AppointmentService
                        .getAvailableDoctorsAtTimeslot(selectedDateTime);
                    if (availableDoctors != null && !availableDoctors.isEmpty()) {
                        return IntStream.range(0, availableDoctors.size())
                            .mapToObj(doctorIndex -> {
                                Doctor doctor = availableDoctors.get(doctorIndex);
                                return new Option(
                                        doctor.getName(),
                                        doctor.getName(),
                                        true,
                                        true
                                    ).setNextMenu(Menu.getUserMainMenu())
                                    .setNextAction((input, args) -> {
                                        AppointmentService.scheduleAppointment(
                                            ((Patient) UserService.getCurrentUser()).getRoleId(),
                                            doctor.getRoleId(),
                                            selectedDateTime
                                        );
                                        return args;
                                    });
                            }).collect(Collectors.toList());
                    }
                }
                MenuService.getCurrentMenu().setNextMenu(() -> Menu.getUserMainMenu());
                throw new Exception("No doctors available.");
            });

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
        String, // user input
        Map<String, Object>, // data from prev menu
        Map<String, Object>, // return updated data
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
        private final boolean requiresConfirmation;
        private MenuGenerator nextMenuGenerator;
        private NextAction nextAction;

        public Option(
            String label,
            String matchPattern,
            boolean isNumberedOption
        ) {
            this(label, matchPattern, isNumberedOption, false);
        };
    
        public Option(
            String label,
            String matchPattern,
            boolean isNumberedOption,
            boolean requiresConfirmation
        ) {
            this.label = label;
            this.matchPattern = matchPattern;
            this.isNumberedOption = isNumberedOption;
            this.requiresConfirmation = requiresConfirmation;
        };

        private NextAction getNextAction() {
            return this.nextAction;
        }

        private MenuGenerator getNextMenuGenerator() {
            return this.nextMenuGenerator;
        }
    
        private Option setNextMenu(Menu nextMenu) {
            if (this.requiresConfirmation) {
                Menu currentMenu = MenuService.getCurrentMenu();
                return this.setNextMenu(() -> Menu.CONFIRM
                    .setOptionGenerator(() -> new ArrayList<>(List.of(
                        new Option(
                            "Yes (Y)",
                            "yes|y|yes( )?\\(?y\\)?",
                            false
                        ).setNextAction(this.getNextAction()).setNextMenu(nextMenu),
                        new Option(
                            "No (N)",
                            "no|n|no( )?\\(?n\\)?",
                            false
                        ).setNextAction((input, args) -> args).setNextMenu(() -> currentMenu)
                    )))
                );
            }
            
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
                .setNextMenu(Menu.getConfirmMenu(nextAction, currentMenu))
            );
        }
    
        private Option setNextAction(NextAction nextAction) {
            this.nextAction = nextAction;
            return this;
        }
    }

    private static int parseUserIntInput(String userInput) throws Exception {
        try {
            return Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            throw new Exception("Please enter an integer/number.");
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

    
    /**
     * @param userInput
     * @return
     * @throws Exception
     * Next state (transition + action) handling START
     * Input: userInput (called from MenuService.handleUserInput)
     * Output: Returns next menu to MenuService along with any form data from current menu 
     */
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
        System.out.println(argsForNext); // TODO: remove test
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

    /**
     * @return
     * @throws Exception
     * 
     */
    private Map<String, Object> executeNextAction() throws Exception {
        if (this.nextAction == null) {
            return null;
        }
            
        return this.nextAction.apply(this.userInput, this.dataFromPreviousMenu);
    }

    public Menu getNextMenu() throws Exception {
        return this.nextMenuGenerator.apply();
    }

    private Menu setNextMenu(Menu nextMenu) { // TODO can we just use this and remove the 2nd one?
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

    private static Menu getConfirmMenu(NextAction nextAction, Menu currentMenu) {
        return Menu.getConfirmMenu(nextAction, currentMenu, MenuService.getCurrentMenu());
    }

    private static Menu getConfirmMenu(
        NextAction nextAction, Menu currentMenu, Menu exitMenu
    ) {
        return Menu.CONFIRM
            .setOptions(new ArrayList<>(List.of(
                new Option(
                        "Yes (Y)",
                        "yes|y|yes( )?\\(?y\\)?",
                        false
                    ).setNextAction((input, args) -> {
                        System.out.println("test");
                        System.out.println(args);
                        try {
                            return nextAction.apply(input, args);
                        } catch (Exception e) {
                            MenuService.setCurrentMenu(currentMenu);
                            throw e;
                        }
                    })
                    .setNextMenu(exitMenu),
                new Option(
                    "No (N)",
                    "no|n|no( )?\\(?n\\)?",
                    false
                ).setNextMenu(currentMenu)
                .setNextAction((input, args) -> args)
            )));
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
                return matcher.find() ? option : null;
            }).filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    // Options handling - display & matching user input END
    // Options handling END
}
