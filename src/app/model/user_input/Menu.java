package app.model.user_input;

import app.constants.AppMetadata;
import app.constants.BloodType;
import app.constants.Gender;
import app.constants.exceptions.ExitApplication;
import app.model.appointments.Appointment;
import app.model.appointments.Appointment.AppointmentStatus;
import app.model.appointments.AppointmentDisplay;
import app.model.appointments.AppointmentOutcomeRecord;
import app.model.appointments.DoctorEvent;
import app.model.appointments.Prescription;
import app.model.appointments.Prescription.PrescriptionStatus;
import app.model.appointments.Timeslot;
import app.model.inventory.Medication;
import app.model.inventory.Request;
import app.model.users.Patient;
import app.model.users.staff.Admin;
import app.model.users.staff.Doctor;
import app.model.users.staff.Pharmacist;
import app.model.users.staff.Staff;
import app.model.users.user_credentials.Email;
import app.model.users.user_credentials.Password;
import app.model.users.user_credentials.PhoneNumber;
import app.model.users.user_credentials.Username;
import app.service.AppointmentService;
import app.service.MedicationService;
import app.service.MenuService;
import app.service.UserService;
import app.service.UserService.SortFilter;
import app.utils.DateTimeUtil;
import app.utils.EnumUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
* @author Rachmiel Teo (@rachtrx)
* @version 1.0
* @since 2024-10-24
*/


public enum Menu {

    // Init START
    EDIT(new MenuBuilder(
        MenuType.INPUT,
        null,
        "Enter a new value: ",
        true
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
    DOCTOR_MAIN_MENU(new MenuBuilder(
        MenuType.SELECT,
        "Doctor Main Menu",
        null
    )),
    ADMIN_MAIN_MENU(new MenuBuilder(
        MenuType.SELECT,
        "Doctor Main Menu",
        null
    )),
    PHARMACIST_MAIN_MENU(new MenuBuilder(
        MenuType.SELECT,
        "Pharmacist Main Menu",
        null
    )),
    SELECT_PATIENT_VIEW_MEDICAL_RECORD(new MenuBuilder(
        MenuType.SELECT,
        "Select Patient To View Medical Record",
        "Enter 'M' or 'Menu' to return to the main menu."
    )),
    SELECT_PATIENT_EDIT_MEDICAL_RECORD(new MenuBuilder(
        MenuType.SELECT,
        "Select Patient To Edit Medical Record",
        "Enter 'M' or 'Menu' to return to the main menu."
    )),
    PATIENT_VIEW_MEDICAL_RECORD(new MenuBuilder(
        MenuType.SELECT,
        "Patient Medical Record",
        "Enter 'M' or 'Menu' to return to the main menu."
    )),
    PATIENT_EDIT_MEDICAL_RECORD(new MenuBuilder( // TODO implement treatments?
        MenuType.SELECT,
        "Patient Medical Record",
        "Please select a field to edit:"
    )),
    PATIENT_APPOINTMENT_SELECTION_TYPE(new MenuBuilder(
        MenuType.SELECT,
        "Choose Date",
        null
    )),
    PATIENT_VIEW_AVAIL_APPOINTMENTS(new MenuBuilder(
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
        null,
        true
    )),
    PATIENT_RESCHEDULE_SELECTION(new MenuBuilder(
        MenuType.SELECT,
        "Choose Appointment to reschedule",
        null
    )),
    PATIENT_CANCEL_SELECTION(new MenuBuilder(
        MenuType.SELECT,
        "Choose Appointment to cancel",
        null,
        true
    )),
    PATIENT_VIEW_CONFIRMED_APPOINTMENTS(new MenuBuilder(
        MenuType.SELECT,
        "Upcoming Scheduled Appointments",
        null
    )),
    PATIENT_VIEW_OUTCOMES(new MenuBuilder(
        MenuType.SELECT,
        "Past Appointment Outcome Records",
        null
    )),
    // TODO: doctor menu
    // DOCTOR_UPDATE_RECORDS(new MenuBuilder(
    //     MenuType.SELECT,
    //     "Past Appointment Outcome Records",
    //     null
    // )),
    DOCTOR_VIEW_SCHEDULE(new MenuBuilder(
        MenuType.SELECT,
        "Doctor Schedule",
        "Select 'M' to return to the main menu."
    )),
    DOCTOR_SET_AVAILABILITY(new MenuBuilder( // TODO
        MenuType.SELECT,
        "Doctor Availability",
        "Set unavailable dates & times"
    )),
    DOCTOR_ACCEPT_APPOINTMENTS(new MenuBuilder(
        MenuType.SELECT,
        "Accept or Decline Appointment Requests",
        "Please select an appointment to accept/reject:"
    )),
    // DOCTOR_VIEW_CONFIRMED(new MenuBuilder(
    //     MenuType.SELECT,
    //     "Past Appointment Outcome Records",
    //     null
    // )),
    // DOCTOR_ADD_OUTCOME(new MenuBuilder(
    //     MenuType.SELECT,
    //     "Past Appointment Outcome Records",
    //     null
    // )),
    SELECT_PATIENT_APPOINTMENT(new MenuBuilder(
        MenuType.SELECT,
        "Edit Appointment",
        "Select an appointment to edit:"
    )),
    EDIT_PATIENT_APPOINTMENT(new MenuBuilder(
        MenuType.SELECT,
        "Edit Appointment",
        "Select a field to edit:"
    )),
    DOCTOR_ACCEPT_OR_DECLINE_APPOINTMENT(new MenuBuilder(
        MenuType.SELECT,
        "Accept or Decline Pending Appointment",
        null,
        true
    )),

    PHARMACIST_VIEW_OUTCOME_RECORDS(new MenuBuilder(
        MenuType.SELECT,
        "All Appointment Outcomes",
        null
    )),

    PHARMACIST_UPDATE_OUTCOMES(new MenuBuilder(
        MenuType.SELECT,
        "All Prescriptions",
        null
    )),

    PHARMACIST_UPDATE_PRESCRIPTIONS(new MenuBuilder(
        MenuType.SELECT,
        "All Prescriptions",
        null
    )),

    PHARMACIST_HANDLE_PRESCRIPTION(new MenuBuilder(
        MenuType.SELECT,
        "Prescription and Medication Order Details",
        null
    )),

    PHARMACIST_ADD_REQUEST(new MenuBuilder(
        MenuType.SELECT,
        "Submit Replenish Request",
        "Please select the medication: "
    )),
    PHARMACIST_ADD_COUNT(new MenuBuilder(
        MenuType.INPUT,
        "Medication Quantity",
        "Please enter the quantity of medication to add: "
    )),

    ADMIN_VIEW_APPOINTMENTS(new MenuBuilder(
        MenuType.SELECT,
        "All Appointments",
        null
    )),
    ADMIN_VIEW_USERS(new MenuBuilder(
        MenuType.SELECT,
        "All Staff",
        null
    )),
    ADMIN_INPUT_USER_EDIT(new MenuBuilder(
        MenuType.INPUT,
        "Edit User",
        "Enter Staff ID"
    )),
    ADMIN_INPUT_USER_DELETE(new MenuBuilder(
        MenuType.INPUT,
        "Delete User",
        "Enter Staff ID",
        true
    )),
    ADMIN_EDIT_USER(new MenuBuilder(
        MenuType.SELECT,
        "User details",
        "Please select a field to edit:"
    )),
    ADMIN_ADD_USER_TYPE(new MenuBuilder( // user start
        MenuType.SELECT,
        "User Roles",
        "Please select a user role to add"
    )),
    ADMIN_ADD_USER_NAME(new MenuBuilder( 
        MenuType.INPUT,
        "Username",
        "Enter Username: "
    )),
    ADMIN_ADD_PASSWORD(new MenuBuilder(
        MenuType.INPUT,
        "Password",
        "Enter Password: "
    )),
    ADMIN_ADD_NAME(new MenuBuilder(
        MenuType.INPUT,
        "Name",
        "Enter Name: "
    )),
    ADMIN_ADD_GENDER(new MenuBuilder( // user end
        MenuType.INPUT,
        "Gender",
        "Enter Gender: "
    )),
    ADMIN_ADD_MOBILE_NO(new MenuBuilder( // patient start
        MenuType.INPUT,
        "Mobile Number",
        "Enter Mobile No.: "
    )),
    ADMIN_ADD_HOME_NO(new MenuBuilder(
        MenuType.INPUT,
        "Home Number",
        "Enter Home No.: "
    )),
    ADMIN_ADD_EMAIL(new MenuBuilder(
        MenuType.INPUT,
        "Email",
        "Enter Email: "
    )),
    ADMIN_ADD_DOB(new MenuBuilder(
        MenuType.INPUT,
        "Date of Birth",
        "Enter Date of Birth: "
    )),
    ADMIN_ADD_BLOODTYPE(new MenuBuilder( // patient end
        MenuType.INPUT,
        "Blood Type",
        "Enter Blood Type: "
    )),
    VIEW_INVENTORY(new MenuBuilder(
        MenuType.SELECT,
        "All Medications",
        null
    )),
    ADMIN_UPDATE_INVENTORY(new MenuBuilder(
        MenuType.SELECT,
        "Edit Medication",
        "Select a medication to edit"
    )),
    ADMIN_ADD_MEDICATION(new MenuBuilder( // patient end
        MenuType.INPUT,
        "Medication Name",
        null
    )),
    ADMIN_ADD_INITIAL_STOCK(new MenuBuilder( // patient end
        MenuType.INPUT,
        "Stock Level",
        null
    )),
    ADMIN_ADD_LOW_LEVEL_ALERT(new MenuBuilder( // patient end
        MenuType.INPUT,
        "Low Level Alert",
        null
    )),
    ADMIN_EDIT_MEDICATION(new MenuBuilder(
        MenuType.SELECT,
        "Medication details",
        "Please select a field to edit:"
    )),
    ADMIN_VIEW_REQUEST(new MenuBuilder(
        MenuType.SELECT,
        "All medication requests",
        "Select a request to approve or reject"
    )),
    HANDLE_REPLENISH_REQUEST(new MenuBuilder(
        MenuType.SELECT,
        "Approve or reject Replenish Request",
        null,
        true
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
                UserService.login((String) args.get("username"), userInput);
                MenuService.getCurrentMenu().setNextMenu(() -> Menu.getUserMainMenu()); // pass in menugenerator
                return null;
            }).setExitMenu(()-> Menu.LOGIN_USERNAME);
        Menu.PATIENT_MAIN_MENU // exit menu should be itself
            .setOptionGenerator(() -> new ArrayList<>(List.of(
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
                        "View Available Appointments", 
                        "view( )?(available( )?)?appointment(s)?", 
                        true
                    ).setNextMenu(() -> PATIENT_VIEW_AVAIL_APPOINTMENTS),
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
                    ),
                new Option(
                        "Reschedule an Appointment", 
                        "^Reschedule( )?(a(n)?( )?)?appointment(s)?",
                        true
                    ).setNextMenu(() -> PATIENT_RESCHEDULE_SELECTION)
                    .setNextAction((a,b) -> new HashMap<String, Object>() {{
                            put("yearValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                            put("monthValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                            put("dayValidator", DateTimeUtil.DateConditions.FUTURE_OR_PRESENT.toString());
                            put("hourValidator", DateTimeUtil.DateConditions.FUTURE.toString());
                        }}
                    ),
                new Option(
                        "Cancel an Appointment", 
                        "^Cancel( )?(a(n)?( )?)?appointment(s)?",
                        true
                    ).setNextMenu(() -> PATIENT_CANCEL_SELECTION)
                    .setNextAction((a,b) -> new HashMap<String, Object>()),
                new Option(
                        "View Scheduled Appointments", 
                        "view( )?(scheduled( )?)?appointment(s)?|(view( )?)?confirmed", 
                        true
                    ).setNextMenu(() -> PATIENT_VIEW_CONFIRMED_APPOINTMENTS),
                new Option(
                        "View Appointment Outcomes", 
                        "view( )?(appointment( )?)?outcomes(s)?|(view( )?)?history", 
                        true
                    ).setNextMenu(() -> PATIENT_VIEW_OUTCOMES)
            ))).shouldAddLogoutOptions();
        Menu.PATIENT_VIEW_MEDICAL_RECORD
            .setDisplayGenerator(() -> {
                Patient patient = Menu.getTargetPatientFromArgs();
                System.out.println("\nPatient Information");
                Menu.printLineBreak(10);
                System.out.println(patient.toString());

                System.out.println("\nAppointment History");
                Menu.printLineBreak(10);
                List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId());
                AppointmentDisplay.printAppointmentDetails(appointments);
            })
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.PATIENT_EDIT_MEDICAL_RECORD
            .setOptionGenerator(() -> {
                Patient patient = Menu.getTargetPatientFromArgs();
                List<Option> result = new ArrayList<>();
                if (
                    List.of(Doctor.class).contains(UserService.getCurrentUser().getClass())
                ) {
                    result.addAll(new ArrayList<>(List.of(
                        // TODO: blood type, diagnosis, appointment records
                        new EditOption(
                            String.format("Blood type: %s", patient.getBloodType()),
                            "blood|type|blood(( )?type)?|(blood( )?)?type",
                            true,
                            (input, args) -> {
                                // patient.setBloodType((String) input); // TODO BUG commented out
                                return null;
                            }
                        ),
                        new Option(
                            "Edit Appointment",
                            "(edit( )?)?appointment",
                            true
                        ).setNextMenu(() -> SELECT_PATIENT_APPOINTMENT)
                        .setNextAction((userInput, args) -> args)
                    )));
                }
                if (
                    List.of(Patient.class, Doctor.class).contains(UserService.getCurrentUser().getClass())
                ) {
                    result.addAll(new ArrayList<>(List.of(
                        new EditOption(
                            String.format("Mobile Number: +65%d", patient.getMobileNumber()),
                            "mobile(( )?number)?",
                            true,
                            (input, args) -> {
                                patient.setMobileNumber((String) input);
                                return args;
                            }
                        ),
                        new EditOption(
                            String.format("Home Number: +65%d", patient.getHomeNumber()),
                            "home(( )?number)?",
                            true,
                            (input, args) -> {
                                patient.setHomeNumber((String) input);
                                return args;
                            }
                        ),
                        new EditOption(
                            String.format("Email: %s", patient.getEmail()),
                            "email",
                            true,
                            (input, args) -> {
                                patient.setEmail((String) input);
                                return args;
                            }
                        )
                    )));
                }
                return result;
            }).shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.PATIENT_VIEW_AVAIL_APPOINTMENTS
            .setDisplayGenerator(() -> {
                    Map<Doctor, List<Timeslot>> timeslotsByDoctor = AppointmentService.getAvailableAppointmentSlotsByDoctor(LocalDateTime.now());
                    // Get tomorrow's timeslots if hospital is closed today
                    if (timeslotsByDoctor.isEmpty()) {
                        timeslotsByDoctor = AppointmentService.getAvailableAppointmentSlotsByDoctor(
                            LocalDateTime.of(LocalDateTime.now().plusDays(1).toLocalDate(), LocalTime.of(0, 0))
                        );
                    }
                    if (!timeslotsByDoctor.isEmpty()) {
                        AppointmentDisplay.printAvailableTimeslots(timeslotsByDoctor);
                    } else {
                        System.out.println("No available timeslots for today and tomorrow.");
                    }       
                })
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.PATIENT_VIEW_CONFIRMED_APPOINTMENTS
            .setDisplayGenerator(() -> {
                Patient patient = (Patient) UserService.getCurrentUser();
                    List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId())
                        .stream()
                        .filter(appointment -> AppointmentStatus.isIn(appointment.getAppointmentStatus(), 
                            AppointmentStatus.CONFIRMED,
                            AppointmentStatus.PENDING)
                        )
                        .collect(Collectors.toList());
                    if (!appointments.isEmpty()) {
                        AppointmentDisplay.printAppointmentDetails(appointments);
                    } else {
                        System.out.println("No appointments scheduled. Start scheduling one today.\n");
                    }
            }).shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.PATIENT_APPOINTMENT_SELECTION_TYPE
            .setOptionGenerator(() -> {
                List<Option> options = new ArrayList<>(Arrays.asList(
                    new Option("Tomorrow", "tmr|tomorrow", true)
                        .setNextAction((userInput, args) -> {
                            LocalDate tmr = LocalDate.now().plusDays(1);
                            args.put("year", String.valueOf(tmr.getYear()));
                            args.put("month", String.valueOf(tmr.getMonthValue()));
                            args.put("day", String.valueOf(tmr.getDayOfMonth()));
                            return args;
                        }).setNextMenu(Menu.INPUT_APPOINTMENT_HOUR), 
                    new Option("Custom", "custom", true)
                    .setNextAction((userInput, args) -> args).setNextMenu(Menu.INPUT_APPOINTMENT_YEAR)
                ));
                if (LocalTime.now().isBefore(Timeslot.lastSlotStartTime)) {
                    options.add(0, new Option("Today", "today", true)
                        .setNextAction((userInput, args) -> {
                            LocalDate today = LocalDate.now();
                            args.put("year", String.valueOf(today.getYear()));
                            args.put("month", String.valueOf(today.getMonthValue()));
                            args.put("day", String.valueOf(today.getDayOfMonth()));
                            return args;
                        }).setNextMenu(Menu.INPUT_APPOINTMENT_HOUR)
                    );
                }
                return options;
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
                args.put(
                    "dateTime", LocalDateTime.of(
                        Menu.parseUserIntInput((String) args.get("year")),
                        Menu.parseUserIntInput((String) args.get("month")),
                        Menu.parseUserIntInput((String) args.get("day")),
                        Menu.parseUserIntInput(userInput), // hour
                        0
                    ));
                return args;
            }).setNextMenu(Menu.INPUT_APPOINTMENT_DOCTOR);
        Menu.INPUT_APPOINTMENT_DOCTOR
            .setOptionGenerator(() -> {
                if (
                    MenuService.getCurrentMenu().dataFromPreviousMenu != null &&
                    MenuService.getCurrentMenu().dataFromPreviousMenu.containsKey("dateTime")
                ) {
                    LocalDateTime selectedDateTime = 
                        (LocalDateTime) MenuService.getCurrentMenu().dataFromPreviousMenu.get("dateTime");
                    List<Doctor> availableDoctors = AppointmentService
                        .getAvailableDoctorsAtTimeslot(selectedDateTime);
                    if (availableDoctors != null && !availableDoctors.isEmpty()) {
                        return IntStream.range(0, availableDoctors.size())
                            .mapToObj(doctorIndex -> {
                                Doctor doctor = availableDoctors.get(doctorIndex);
                                return new Option(
                                    doctor.getName(),
                                    doctor.getName(),
                                    true
                                ).setNextMenu(Menu.getUserMainMenu())
                                .setNextAction((input, args) -> {
                                    if (args.get("currentAppointment") != null) {
                                        AppointmentService.rescheduleAppointment(
                                            ((Patient) UserService.getCurrentUser()).getRoleId(),
                                            doctor.getRoleId(),
                                            selectedDateTime,
                                            (Appointment) args.get("currentAppointment")
                                        );
                                    } else {
                                        AppointmentService.scheduleAppointment(
                                            ((Patient) UserService.getCurrentUser()).getRoleId(),
                                            doctor.getRoleId(),
                                            selectedDateTime
                                        );
                                    }
                                    return args;
                                });
                            }).collect(Collectors.toList());
                    }
                }
                MenuService.getCurrentMenu().setNextMenu(() -> Menu.getUserMainMenu());
                throw new Exception("No doctors available.");
            }).setExitMenu(() -> Menu.getUserMainMenu());
        Menu.PATIENT_RESCHEDULE_SELECTION
            .setOptionGenerator(() -> {
                Patient patient = (Patient) UserService.getCurrentUser();
                List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId())
                    .stream()
                    .filter(appointment -> new ArrayList<Appointment.AppointmentStatus>(){{
                        add(AppointmentStatus.CONFIRMED);
                        add(AppointmentStatus.PENDING);
                    }}.contains(appointment.getAppointmentStatus()))
                    .collect(Collectors.toList());
                if (appointments != null && !appointments.isEmpty()) {
                    return IntStream.range(0, appointments.size())
                        .mapToObj(apptIdx -> {
                            Appointment appointment = appointments.get(apptIdx);
                            return new Option(
                                DateTimeUtil.printLongDateTime(appointment.getTimeslot()),
                                DateTimeUtil.printShortDateTime(appointment.getTimeslot()),
                                true
                            ).setNextMenu(Menu.PATIENT_APPOINTMENT_SELECTION_TYPE)
                            .setNextAction((input, args) -> {
                                args.put("currentAppointment", appointment);
                                return args;
                            });
                        }).collect(Collectors.toList());
                }
                MenuService.getCurrentMenu().setNextMenu(() -> Menu.getUserMainMenu());
                throw new Exception("No appointments available.");
            }).setExitMenu(() -> Menu.getUserMainMenu());
        Menu.PATIENT_CANCEL_SELECTION
            .setOptionGenerator(() -> {
                Patient patient = (Patient) UserService.getCurrentUser();
                List<Appointment> appointments = AppointmentService.getAllAppointmentsForPatient(patient.getRoleId())
                    .stream()
                    .filter(appointment -> appointment.getAppointmentStatus() == Appointment.AppointmentStatus.CONFIRMED)
                    .collect(Collectors.toList());
                if (appointments != null && !appointments.isEmpty()) {
                    return IntStream.range(0, appointments.size())
                        .mapToObj(apptIdx -> {
                            Appointment appointment = appointments.get(apptIdx);
                            return new Option(
                                DateTimeUtil.printLongDateTime(appointment.getTimeslot()),
                                DateTimeUtil.printShortDateTime(appointment.getTimeslot()),
                                true
                            ).setNextMenu(() -> Menu.PATIENT_MAIN_MENU)
                            .setNextAction((input, args) -> {
                                AppointmentService.cancelAppointment(appointment);
                                return args;
                            });
                        }).collect(Collectors.toList());
                }
                MenuService.getCurrentMenu().setNextMenu(() -> Menu.getUserMainMenu());
                throw new Exception("No appointments available.");
            }).setExitMenu(() -> Menu.getUserMainMenu());
        Menu.PATIENT_VIEW_OUTCOMES
            .setDisplayGenerator(() -> {
                Patient patient = (Patient) UserService.getCurrentUser();
                List<AppointmentOutcomeRecord> outcomeRecords = AppointmentService.getAppointmentRecordsByPatientId(patient.getRoleId());
                if (!outcomeRecords.isEmpty()) {
                    outcomeRecords.stream().forEach(outcomeRecord -> 
                        AppointmentDisplay.printAppointmentOutcomeDetails(outcomeRecord)
                    );
                } else {
                    System.out.println("No appointment outcomes found. Start scheduling an appointment today.\n");
                }
            }).setExitMenu(() -> Menu.getUserMainMenu())
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.DOCTOR_MAIN_MENU // exit menu should be itself
            .setOptionGenerator(() -> new ArrayList<>(List.of(
                new Option(
                        "View Patient's Medical Record", 
                        "view( )?(patient(\\'s)?( )?')?(medical( )?)?record", 
                        true
                    ).setNextMenu(() -> SELECT_PATIENT_VIEW_MEDICAL_RECORD),
                new Option(
                        "Update Patient's Medical Record", 
                        "(edit( )?)?(patient( )?('s)?)?(medical( )?)?record", 
                        true
                    ).setNextMenu(() -> SELECT_PATIENT_EDIT_MEDICAL_RECORD),
                new Option(
                        "View Personal Schedule", 
                        "(view( )?)?(personal( )?)?schedule", 
                        true
                    ).setNextMenu(() -> DOCTOR_VIEW_SCHEDULE),
                new Option(
                        "Set Appointment Availability", 
                        "(set( )?)?(appointment( )?)?availability", 
                        true
                    ).setNextMenu(() -> DOCTOR_SET_AVAILABILITY),
                new Option(
                        "Accept or Decline Appointment Requests", 
                        "accept|decline|(appointment)?( )?requests", 
                        true
                    ).setNextMenu(() -> DOCTOR_ACCEPT_APPOINTMENTS)
            ))).shouldAddLogoutOptions();
        Menu.SELECT_PATIENT_VIEW_MEDICAL_RECORD
            .setPatientListOptionGenerator(() -> PATIENT_VIEW_MEDICAL_RECORD)
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.SELECT_PATIENT_EDIT_MEDICAL_RECORD
            .setPatientListOptionGenerator(() -> PATIENT_EDIT_MEDICAL_RECORD)
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.SELECT_PATIENT_APPOINTMENT
            .setOptionGenerator(() -> {
                List<Option> options = AppointmentService
                    .getAllAppointmentsForPatient(Menu.getTargetPatientFromArgs().getRoleId())
                    .stream()
                    .filter(appointment -> appointment.getAppointmentStatus().equals(AppointmentStatus.COMPLETED))
                    .map(appointment -> 
                        new Option(
                            String.format(
                                "%s (Status: %s)",
                                DateTimeUtil.printLongDateTime(appointment.getTimeslot()),
                                appointment.getAppointmentStatus().toString()
                            ),
                            DateTimeUtil.printLongDateTime(appointment.getTimeslot()),
                            true
                        ).setNextAction((userInput, args) -> new HashMap<String, Object>(){{
                            put("appointmentId", Integer.toString(appointment.getAppointmentId()));
                        }}).setNextMenu(() -> EDIT_PATIENT_APPOINTMENT)
                    ).collect(Collectors.toList());
                    if (options.isEmpty()) {
                        throw new Exception("No confirmed appointments found.");
                    }
                return options;
            }).shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.EDIT_PATIENT_APPOINTMENT
            .setOptionGenerator(() -> {
                Appointment appointment = null;
                try {
                    appointment = AppointmentService.getAppointment(
                        Integer.parseInt(
                            (String) MenuService.getCurrentMenu().dataFromPreviousMenu.get("appointmentId")
                        )
                    );
                } catch (Exception e) {}
                if (appointment == null) {
                    throw new Exception("Appointment not found");
                }
                return new ArrayList<>(List.of(
                    // TODO: redirect to appointment accept
                    // new Option(
                    //     appointment.getAppointmentStatus().toString(),
                    //     appointment.getAppointmentStatus().toString(),
                    //     true
                    // ).setNextAction((userInput, args) -> args)
                    // .setNextMenu(() -> CHANGE_APPOINTMENT_STATUS)),
                    // TODO: redirect to update prescription
                    // new Option(appointment., null, false)
                ));
            }).shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.DOCTOR_VIEW_SCHEDULE
            .setOptionGenerator(() -> {
                // AppointmentService.getAllEvents().stream().forEach(event -> System.out.println(event.getDoctorId())); // BUG due to updated Appointment Service code
                System.out.println(UserService.getCurrentUser().getRoleId());
                List<Option> options = AppointmentService.getAllAppointmentsForDoctor(UserService.getCurrentUser().getRoleId())
                    .stream()
                    .filter(event ->
                        event.getDoctorId() == UserService.getCurrentUser().getRoleId()
                        // &&
                        // event.getTimeslot().plusHours(Timeslot.timeslotLengthInHours)
                        //     .isAfter(LocalDateTime.now())
                    ).sorted(Comparator.comparing(DoctorEvent::getTimeslot))
                    .map(event -> new Option(
                            DateTimeUtil.printLongDateTime(event.getTimeslot()),
                            "\n",
                            true
                        ).setNextAction((userInput, args) -> {
                            MenuService.setCurrentMenu(Menu.getUserMainMenu());
                            throw new Exception("");
                        })
                    ).collect(Collectors.toList());
                if (options == null || options.isEmpty()) {
                    MenuService.setCurrentMenu(Menu.getUserMainMenu());
                    throw new Exception("No upcoming events scheduled.");
                }
                return options;
            }).setNextMenu(() -> Menu.getUserMainMenu())
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.DOCTOR_SET_AVAILABILITY
            // .setOptionGenerator(() -> new ArrayList<>(List.of(
            //     new Option(
            //             "View Unavailable Periods", 
            //             "view(( )?unavailable)?(( )?periods)?", 
            //             true
            //         ).setNextMenu(() -> DOCTOR_VIEW_UNAVAILABLE),
            //     new Option(
            //             "Add Unavailable Periods", 
            //             "add(( )?unavailable)?(( )?periods)?", 
            //             true
            //         ).setNextMenu(() -> DOCTOR_ADD_UNAVAILABLE),
            //     new Option(
            //             "Delete Unavailable Periods", 
            //             "delete(( )?unavailable)?(( )?periods)?", 
            //             true
            //         ).setNextMenu(() -> DOCTOR_DELETE_UNAVAILABLE),
            //     new Option(
            //             "Edit Unavailable Periods", 
            //             "edit(( )?unavailable)?(( )?periods)?", 
            //             true
            //         ).setNextMenu(() -> DOCTOR_EDIT_UNAVAILABLE)
            // ))).setNextMenu(() -> Menu.getUserMainMenu())
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.DOCTOR_ACCEPT_APPOINTMENTS
            .setOptionGenerator(() -> {
                List<Option> options = AppointmentService
                    .getAppointmentByStatus(AppointmentStatus.PENDING)
                    .stream()
                    .filter(appointment ->
                        appointment.getDoctorId() == UserService.getCurrentUser().getRoleId()
                    ).sorted(Comparator.comparing(Appointment::getTimeslot))
                    .map(appointment -> {
                        Patient patient = UserService.findUserByIdAndType(
                            appointment.getPatientId(),
                            Patient.class,
                            true
                        );
                        String timeslot = DateTimeUtil.printLongDateTime(appointment.getTimeslot());
                        return new Option(
                            patient == null ? timeslot : String.format(
                                "%s - %s (P%d)",
                                timeslot,
                                patient.getName(),
                                patient.getRoleId()
                            ),
                            timeslot,
                            true
                        ).setNextAction((userInput, args) -> {
                            args = args == null ? new HashMap<>() : args;
                            args.put("appointmentId", Integer.toString(appointment.getAppointmentId()));
                            return args;
                        }).setNextMenu(() -> DOCTOR_ACCEPT_OR_DECLINE_APPOINTMENT);
                }).collect(Collectors.toList());
                if (options == null || options.isEmpty()) {
                    MenuService.setCurrentMenu(Menu.getUserMainMenu());
                    throw new Exception("No pending appointments.");
                }
                return options;
            })
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.DOCTOR_ACCEPT_OR_DECLINE_APPOINTMENT
            .setOptionGenerator(() -> new ArrayList<>(List.of(
                new Option("Accept", "accept", true)
                    .setNextMenu(() -> DOCTOR_ACCEPT_APPOINTMENTS)
                    .setNextAction((input, args) -> {
                        AppointmentService
                            .getAppointment(Integer.parseInt((String)args.get("appointmentId")))
                            .confirm();
                        return null;
                    }),
                new Option("Decline", "decline", true)
                    .setNextMenu(() -> DOCTOR_ACCEPT_APPOINTMENTS)
                    .setNextAction((input, args) -> {
                        AppointmentService
                            .getAppointment(Integer.parseInt((String)args.get("appointmentId")))
                            .cancel();
                        return null;
                    })
            ))).setExitMenu(() -> Menu.getUserMainMenu())
            .shouldAddMainMenuOption()
            .shouldAddLogoutOptions();
        Menu.PHARMACIST_MAIN_MENU
            .setOptionGenerator(() -> new ArrayList<>(List.of(
                new Option(
                        "View Appointment Outcomes", 
                        "(view( )?)?outcomes(s)?", 
                        true
                    ).setNextMenu(() -> PHARMACIST_VIEW_OUTCOME_RECORDS)
                    .setNextAction((a, b) -> new HashMap<String, Object>() {{
                        put("hideCompleted", false);
                    }}),
                new Option(
                        "Update Prescriptions", 
                        "update( )?prescription(s)?", 
                        true
                    ).setNextMenu(() -> PHARMACIST_UPDATE_OUTCOMES),
                new Option(
                        "Submit Replenish Request", 
                        "submit( )?request(s)?",
                        true
                    ).setNextMenu(() -> PHARMACIST_ADD_REQUEST),
                new Option(
                        "View Inventory", 
                        "(view( )?)?inventory", 
                        true
                    ).setNextMenu(() -> VIEW_INVENTORY)
            ))).shouldAddLogoutOptions();

        Menu.PHARMACIST_VIEW_OUTCOME_RECORDS
            .setDisplayGenerator(() -> {
                Map<String, Object> values = MenuService.getCurrentMenu().dataFromPreviousMenu;
                final Boolean hideCompleted = values != null && values.containsKey("hideCompleted") && (Boolean) values.get("hideCompleted");

                List<String> displayData = AppointmentService.getAllAppointments().stream()
                    .filter(appointment -> appointment.getAppointmentOutcome() != null)
                    .filter(appointment -> {
                        if (hideCompleted) {
                            PrescriptionStatus status = appointment.getAppointmentOutcome().getPrescription().getStatus();
                            return !status.equals(PrescriptionStatus.DISPENSED);
                        } else {
                            return true;
                        }
                    })
                    .map(appointment -> {
                        AppointmentOutcomeRecord outcome = appointment.getAppointmentOutcome();
                        return String.join(" | ", 
                            "Appointment ID: " + appointment.getAppointmentId(),
                            "Patient ID: " + appointment.getPatientId(), 
                            "Doctor ID: " + appointment.getDoctorId(), 
                            "Notes: " + outcome.getConsultationNotes(),
                            "Medications: " + outcome.getPrescription().getMedicationOrders().size(),
                            "Prescription Status: " + outcome.getPrescription().getStatus().toString()
                        );
                    })
                    .collect(Collectors.toList());
            
                displayData.forEach(System.out::println);
            })
            .setOptionGenerator(() -> {
                Map<String, Object> values = MenuService.getCurrentMenu().dataFromPreviousMenu;
                final Boolean hideCompleted = values != null && values.containsKey("hideCompleted") && (Boolean) values.get("hideCompleted");
            
                List<Option> options = new ArrayList<>();
            
                options.add(new Option(
                    "Update Prescription",
                    "edit( )?prescription(s)?",
                    true
                ).setNextMenu(() -> PHARMACIST_UPDATE_OUTCOMES));

                if(options.isEmpty()) throw new Exception("No Appointment Outcomes found.");
            
                options.add(
                    hideCompleted
                        ? new Option(
                            "Show Completed",
                            "show( )?(completed)?",
                            true
                        ).setNextAction((a, b) -> new HashMap<String, Object>() {{
                            put("hideCompleted", false);
                        }}).setNextMenu(PHARMACIST_VIEW_OUTCOME_RECORDS)
                        : new Option(
                            "Hide Completed",
                            "hide( )?(completed)?",
                            true
                        ).setNextAction((a, b) -> new HashMap<String, Object>() {{
                            put("hideCompleted", true);
                        }}).setNextMenu(PHARMACIST_VIEW_OUTCOME_RECORDS)
                );
                
                return options;
            })
            .shouldAddLogoutOptions()
            .shouldAddMainMenuOption();

        Menu.PHARMACIST_UPDATE_OUTCOMES
            .setOptionGenerator(() -> {
                // Generate options based on appointments
                List<Option> options = AppointmentService.getAllAppointments().stream()
                    .filter(appointment -> appointment.getAppointmentOutcome() != null)
                    .filter(appointment -> !appointment.getAppointmentOutcome().getPrescription().getStatus().equals(PrescriptionStatus.DISPENSED))
                    .map(appointment -> {
                        AppointmentOutcomeRecord outcome = appointment.getAppointmentOutcome();
                        String displayText = String.join(" | ", 
                            "Appointment ID: " + appointment.getAppointmentId(),
                            "Patient ID: " + appointment.getPatientId(), 
                            "Doctor ID: " + appointment.getDoctorId(), 
                            "Notes: " + outcome.getConsultationNotes(),
                            "Medications: " + outcome.getPrescription().getMedicationOrders().size(),
                            "Prescription Status: " + outcome.getPrescription().getStatus().toString()
                        );
                        return new Option(
                            displayText,
                            "(update)?( )?" + appointment.getAppointmentId(),
                            true
                        ).setNextAction((a, b) -> {
                            Map<String, Object> args = new HashMap<>();
                            args.put("prescription", outcome.getPrescription());
                            return args;
                        }).setNextMenu(PHARMACIST_HANDLE_PRESCRIPTION);
                    })
                    .collect(Collectors.toList());
                if(options.isEmpty()) throw new Exception("No prescriptions to update.");
                return options;
            });

        Menu.PHARMACIST_UPDATE_PRESCRIPTIONS
            .setOptionGenerator(() -> {
                // Generate options based on appointments
                List<Option> options = AppointmentService.getAllAppointments().stream()
                    .filter(appointment -> appointment.getAppointmentOutcome() != null)
                    .filter(appointment -> !appointment.getAppointmentOutcome().getPrescription().getStatus().equals(PrescriptionStatus.DISPENSED))
                    .map(appointment -> {
                        Prescription prescription = appointment.getAppointmentOutcome().getPrescription();
                        String displayText = String.join(" | ", 
                            "Outcome ID: " + prescription.getOutcomeId(),
                            "Medications: " + prescription.getMedicationOrders().size(), 
                            "Prescription Status: " + prescription.getStatus().toString()
                        );
                        return new Option(
                            displayText,
                            "(update)?( )?" + appointment.getAppointmentId(),
                            true
                        ).setNextAction((a, b) -> {
                            Map<String, Object> args = new HashMap<>();
                            args.put("prescription", prescription);
                            return args;
                        }).setNextMenu(PHARMACIST_HANDLE_PRESCRIPTION);
                    })
                    .collect(Collectors.toList());
                
                if(options.isEmpty()) throw new Exception("No prescriptions to update.");
                return options;
            }).setExitMenu(() -> PHARMACIST_VIEW_OUTCOME_RECORDS);
        
        Menu.PHARMACIST_HANDLE_PRESCRIPTION.setDisplayGenerator(() -> {
                Map<String, Object> values = MenuService.getCurrentMenu().dataFromPreviousMenu;
                if(values == null || !values.containsKey("prescription")) throw new Exception("Prescription not found");
                Prescription prescription = (Prescription) values.get("prescription");

                if (prescription != null) {
                    // Prescription information in a single row with | separator
                    String prescriptionInfo = String.join(" | ",
                        "Prescription ID: " + prescription.getId(),
                        "Outcome ID: " + prescription.getOutcomeId(),
                        "Status: " + prescription.getStatus().toString()
                    );
                
                    // Medication orders information, each order in a single row, separated from prescription
                    String medicationOrdersInfo = prescription.getMedicationOrders().stream()
                        .map(order -> String.join(" | ",
                            "Order ID: " + order.getId(),
                            "Medication ID: " + order.getMedicationId(),
                            "Quantity: " + order.getQuantity(),
                            "Prescription ID: " + order.getPrescriptionId()
                        ))
                        .collect(Collectors.joining("\n"));

                    String displayData = "Prescription Details:\n" + prescriptionInfo + "\n\nMedications:\n" + medicationOrdersInfo;
                    System.out.println(displayData);
                }
            })
            .setOptionGenerator(() -> {
                Map<String, Object> values = MenuService.getCurrentMenu().dataFromPreviousMenu;
                if(values == null || !values.containsKey("prescription")) throw new Exception("Prescription not found");
                Prescription prescription = (Prescription) values.get("prescription");

                return Arrays.stream(PrescriptionStatus.values())
                    .filter(status -> status.ordinal() > prescription.getStatus().ordinal()) // Only statuses after the current one
                    .map(status -> new Option(
                            status.toString(),
                            status.toString().toLowerCase(),
                            true
                        ).setNextAction((userInput, args) -> {
                            Prescription p = (Prescription) args.get("prescription");
                            p.setStatus(status);
                            return null;
                        }).setNextMenu(PHARMACIST_VIEW_OUTCOME_RECORDS)
                    ).collect(Collectors.toList());
            })
            .setNextAction((input, args) -> {
                PrescriptionStatus pStatus = EnumUtils.fromString(PrescriptionStatus.class, input);
                Prescription p = (Prescription) args.get("prescription");
                p.setStatus(pStatus);
                return null;
            })
            .shouldAddLogoutOptions().shouldAddMainMenuOption();

        Menu.PHARMACIST_ADD_REQUEST
            .setOptionGenerator(() -> {
                return MedicationService.getAllMedications().stream()
                    .map(medication -> new Option(
                        medication.getName() + " | ID: " + medication.getId(),
                        "select medication " + medication.getId(),
                        true
                    ).setNextAction((a, b) -> {
                        Map<String, Object> args = new HashMap<>();
                        args.put("medicationId", medication.getId());
                        return args;
                    }).setNextMenu(PHARMACIST_ADD_COUNT))
                    .collect(Collectors.toCollection(ArrayList::new)); // Collect into a mutable ArrayList
            });
                
        Menu.PHARMACIST_ADD_COUNT
            .setNextMenu(VIEW_INVENTORY)
            .setNextAction((userInput, args) -> {
                Integer medicationId = (Integer) args.get("medicationId");
                MedicationService.submitReplenishRequest(medicationId, Integer.parseInt(userInput));
                return null;
            });
        
        Menu.ADMIN_MAIN_MENU
            .setOptionGenerator(() -> new ArrayList<>(List.of(
                new Option(
                        "View Users", 
                        "(view( )?)?user(s)?", 
                        true
                    ).setNextMenu(() -> ADMIN_VIEW_USERS)
                    .setNextAction((a, b) -> new HashMap<String, Object>() {{
                        put("filter", SortFilter.ROLE);
                        put("asc", true);
                    }}),
                new Option(
                        "View Appointments", 
                        "view( )?appointment(s)?", 
                        true
                    ).setNextMenu(() -> ADMIN_VIEW_APPOINTMENTS),
                new Option(
                        "Add User", 
                        "add( )?user(s)?",
                        true
                    ).setNextMenu(() -> ADMIN_ADD_USER_TYPE),
                new Option(
                        "View Inventory", 
                        "(view( )?)?inventory", 
                        true
                    ).setNextMenu(() -> VIEW_INVENTORY),
                new Option(
                        "View Requests", 
                        "(view( )?)?requests(s)?",
                        true
                    ).setNextMenu(() -> ADMIN_VIEW_REQUEST)
            ))).shouldAddLogoutOptions();
        Menu.ADMIN_VIEW_APPOINTMENTS.setDisplayGenerator(() -> {
                List<Appointment> appointments = AppointmentService.getAllAppointments()
                    .stream()
                    .collect(Collectors.toList());
                if (!appointments.isEmpty()) {
                    AppointmentDisplay.printAppointmentDetails(appointments);
                } else {
                    System.out.println("No appointments scheduled.\n");
                }
        }).shouldAddMainMenuOption()
        .shouldAddLogoutOptions();
        
        Menu.ADMIN_VIEW_USERS
            .setDisplayGenerator(() -> {
                Map<String, Object> values = MenuService.getCurrentMenu().dataFromPreviousMenu;
                if (
                    values != null && values.containsKey("filter") && values.containsKey("asc")
                ) {
                    SortFilter filter = (SortFilter) values.get("filter");
                    UserService.printUserDetailsAsTable(filter, (Boolean) values.get("asc"));
                } else UserService.printUserDetailsAsTable(SortFilter.ROLE, true);
            })
            .setOptionGenerator(() -> {
                List<Option> options = new ArrayList<>();

                Map<String, Object> values = MenuService.getCurrentMenu().dataFromPreviousMenu;
                if (
                    values != null && values.containsKey("filter") && values.containsKey("asc")
                ) {
                    SortFilter filter = (SortFilter) values.get("filter");
                    Boolean asc = (Boolean) values.get("asc");

                    if (filter != SortFilter.ROLE || asc != true) {  // Replace with your actual condition
                        options.add(new Option(
                            "Sort Role Asc", 
                            "role( )?(asc)?", 
                            true
                        ).setNextMenu(() -> ADMIN_VIEW_USERS)
                        .setNextAction((input, args) -> {
                            args.put("filter", SortFilter.ROLE);
                            args.put("asc", true);
                            return args;
                        }));
                    }
                    if (filter != SortFilter.ROLE || asc != false) {
                        options.add(new Option(
                            "Sort Role Desc", 
                            "role( )?desc", 
                            true
                        ).setNextMenu(() -> ADMIN_VIEW_USERS)
                        .setNextAction((input, args) -> {
                            args.put("filter", SortFilter.ROLE);
                            args.put("asc", false);
                            return args;
                        }));
                    }
                    
                    if (filter != SortFilter.GENDER || asc != true) { 
                        options.add(new Option(
                            "Sort Gender Asc", 
                            "gender( )?(asc)?", 
                            true
                        ).setNextMenu(() -> ADMIN_VIEW_USERS)
                        .setNextAction((input, args) -> {
                            args.put("filter", SortFilter.GENDER);
                            args.put("asc", true);
                            return args;
                        }));
                    }
                    if (filter != SortFilter.GENDER || asc != false) {
                        options.add(new Option(
                            "Sort Gender Desc", 
                            "gender( )?desc", 
                            true
                        ).setNextMenu(() -> ADMIN_VIEW_USERS)
                        .setNextAction((input, args) -> {
                            args.put("filter", SortFilter.GENDER);
                            args.put("asc", false);
                            return args;
                        }));
                    }
    
                    if (filter != SortFilter.AGE || asc != true) {  // Replace with your actual condition
                        options.add(new Option(
                            "Sort Age Asc", 
                            "age( )?(asc)?", 
                            true
                        ).setNextMenu(() -> ADMIN_VIEW_USERS)
                        .setNextAction((input, args) -> {
                            args.put("filter", SortFilter.AGE);
                            args.put("asc", true);
                            return args;
                        }));
                    }
                    if (filter != SortFilter.AGE || asc != false) { 
                        options.add(new Option(
                            "Sort Age Desc", 
                            "age( )?desc", 
                            true
                        ).setNextMenu(() -> ADMIN_VIEW_USERS)
                        .setNextAction((input, args) -> {
                            args.put("filter", SortFilter.AGE);
                            args.put("asc", false);
                            return args;
                        }));
                    }

                    options.addAll(List.of(
                        new Option(
                            "Add a user", 
                            "add( )?user(s)?",
                            true
                        ).setNextMenu(() -> ADMIN_ADD_USER_TYPE),

                        new Option(
                            "Edit a user", 
                            "edit( )?user",
                            true
                        ).setNextMenu(() -> ADMIN_INPUT_USER_EDIT),

                        new Option(
                            "Delete a user", 
                            "del(ete)?( )?user",
                            true
                        ).setNextMenu(() -> ADMIN_INPUT_USER_DELETE)
                    ));
                }
                return options;
            })
            .shouldAddMainMenuOption();

        Menu.ADMIN_INPUT_USER_EDIT
            .setNextMenu(ADMIN_EDIT_USER)
            .setNextAction((userInput, args) -> {
                int userIntInput = Menu.parseUserIntInput(userInput);
                Staff s = UserService.findStaffById(userIntInput);
                if (s == null) throw new Exception("Staff not found!");
                args.put("staffId", s.getStaffId());
                return args;
            });

        Menu.ADMIN_INPUT_USER_DELETE
            .setNextMenu(ADMIN_VIEW_USERS)
            .setNextAction((userInput, args) -> {
                int userIntInput = Menu.parseUserIntInput(userInput);
                Staff s = UserService.findStaffById(userIntInput);
                if (s == null) throw new Exception("Staff not found!");
                UserService.deleteStaff(s);
                return null;
            });
                

        Menu.ADMIN_EDIT_USER
            .setOptionGenerator(() -> {
                Map<String, Object> values = MenuService.getCurrentMenu().dataFromPreviousMenu;
                if (values != null && values.containsKey("staffId")) {
                    Staff s = UserService.findStaffById((int) values.get("staffId"));
                    if (s == null) throw new Exception("Staff not found!");

                    return new ArrayList<>(List.of(
                        new EditOption(
                            String.format("Username: %s", s.getUsername()),
                            "username",
                            true,
                            (input, args) -> {
                                s.setUsername((String) input);
                                return null;
                            }
                        ),
                        new EditOption(
                            String.format("Password: %s", "*".repeat(s.getPassword().length())),
                            "password",
                            true,
                            (input, args) -> {
                                s.setPassword((String) input);
                                return null;
                            }
                        ),
                        new EditOption(
                            String.format("Name: %s", s.getName()),
                            "name",
                            true,
                            (input, args) -> {
                                s.setName((String) input);
                                return null;
                            }
                        ),
                        new EditOption( // TODO possible to create new selection menu for gender...
                            String.format("Gender: %s", s.getGender()),
                            "gender",
                            true,
                            (input, args) -> {
                                s.setGender((String) input);
                                return null;
                            }
                        )
                    ));
                } else throw new Exception("Staff not found!");
            });
        
        Menu.ADMIN_ADD_USER_TYPE
            .setOptionGenerator(() -> new ArrayList<>(List.of(
                createRoleOption(Patient.class),
                createRoleOption(Doctor.class),
                createRoleOption(Pharmacist.class),
                createRoleOption(Admin.class)
            )));
        
        Menu.ADMIN_ADD_USER_NAME.setNextMenu(ADMIN_ADD_PASSWORD)
            .setNextAction((userInput, args) -> {
                Username u = new Username(userInput); // validate
                args.put("userName", userInput);
                return args;
            });
        
        Menu.ADMIN_ADD_PASSWORD.setNextMenu(ADMIN_ADD_NAME)
            .setNextAction((userInput, args) -> {
                Password p = new Password(userInput); // validate
                args.put("password", userInput);
                return args;
            });
        
        Menu.ADMIN_ADD_NAME.setNextMenu(ADMIN_ADD_GENDER)
            .setNextAction((userInput, args) -> {
                args.put("name", userInput);
                return args;
            });
        
        Menu.ADMIN_ADD_GENDER.setNextMenu(ADMIN_ADD_DOB)
            .setDisplayGenerator(() -> {
                System.out.println(Arrays.toString(Gender.values()));
            })
            .setNextAction((userInput, args) -> {
                Gender g = EnumUtils.fromString(Gender.class, userInput);
                args.put("gender", userInput);
                return args;
            });

        Menu.ADMIN_ADD_DOB.setNextMenu(() -> {
                Map<String, Object> values = MenuService.getCurrentMenu().dataFromPreviousMenu;

                if (values != null && values.containsKey("role")) {
                    values.forEach((key, value) -> {
                        if (value instanceof String) {
                            System.out.println("Key: " + key + ", Value: " + value);
                        }
                    });
                    if (!values.get("role").equals(Patient.class.getSimpleName())) return ADMIN_VIEW_USERS;
                    else return ADMIN_ADD_MOBILE_NO;
                } else throw new Exception("User Type unknown");
            }).setNextAction((userInput, args) -> {
                if (args.get("role").equals(Patient.class.getSimpleName())) {
                    LocalDate d = DateTimeUtil.parseShortDate(userInput);
                    args.put("dob", userInput);
                    return args;
                } else if (args.get("role").equals(Doctor.class.getSimpleName())) {
                    Doctor d = Doctor.create(
                        (String) args.get("userName"),
                        (String) args.get("password"),
                        (String) args.get("name"),
                        (String) args.get("gender"),
                        userInput
                    );
                    UserService.addUsers(List.of(d));
                    return null;
                } else if (args.get("role").equals(Pharmacist.class.getSimpleName())) {
                    Pharmacist p = Pharmacist.create(
                        (String) args.get("userName"),
                        (String) args.get("password"),
                        (String) args.get("name"),
                        (String) args.get("gender"),
                        userInput
                    );
                    UserService.addUsers(List.of(p));
                    return null;
                } else if (args.get("role").equals(Admin.class.getSimpleName())) {
                    Admin a = Admin.create(
                        (String) args.get("userName"),
                        (String) args.get("password"),
                        (String) args.get("name"),
                        (String) args.get("gender"),
                        userInput
                    );
                    UserService.addUsers(List.of(a));
                    return null;
                } else throw new Exception("Type not found!");
            });
        
        Menu.ADMIN_ADD_MOBILE_NO.setNextMenu(ADMIN_ADD_HOME_NO)
            .setNextAction((userInput, args) -> {
                PhoneNumber mNumber = new PhoneNumber(userInput);
                args.put("mobileNumber", userInput);
                return args;
            });
        
        Menu.ADMIN_ADD_HOME_NO.setNextMenu(ADMIN_ADD_EMAIL)
            .setNextAction((userInput, args) -> {
                PhoneNumber hNumber = new PhoneNumber(userInput);
                args.put("homeNumber", userInput);
                return args;
            });
        
        Menu.ADMIN_ADD_EMAIL.setNextMenu(ADMIN_ADD_BLOODTYPE)
            .setNextAction((userInput, args) -> {
                Email e = new Email(userInput);
                args.put("email", userInput);
                return args;
            });
        
        Menu.ADMIN_ADD_BLOODTYPE.setNextMenu(ADMIN_VIEW_USERS)
            .setDisplayGenerator(() -> {
                System.out.println(Arrays.toString(BloodType.values()));
            })
            .setNextAction((userInput, args) -> {
                Patient p = Patient.create(
                    (String) args.get("userName"),
                    (String) args.get("password"),
                    (String) args.get("name"),
                    (String) args.get("gender"),
                    (String) args.get("dob"),
                    (String) args.get("mobileNumber"),
                    (String) args.get("homeNumber"),
                    (String) args.get("email"),
                    userInput
                );
                return null;
            });
        
        Menu.VIEW_INVENTORY
            .setDisplayGenerator(() -> {
                List<String> displayData = MedicationService.getAllMedications().stream()
                    .map(medication -> String.join(" | ", 
                        "Medication ID: " + medication.getId(), 
                        "Name: " + medication.getName(), 
                        "Stock: " + medication.getStock(), 
                        "Low Alert Level: " + medication.getLowAlertLevel()
                    ))
                    .collect(Collectors.toList());
                displayData.forEach(System.out::println);
            })
            .setOptionGenerator(() -> {
                Boolean isAdmin = UserService.getCurrentUser() instanceof Admin;
                if (isAdmin) {
                    return new ArrayList<>(List.of(
                        new Option(
                            "Edit medication", 
                            "edit( )?medication(s)?",
                            true
                        ).setNextMenu(() -> ADMIN_UPDATE_INVENTORY)
                        .setNextAction((a, b) -> new HashMap<String, Object>() {{
                            put("control", Control.EDIT);
                        }})
                    ));
                } else {
                    return new ArrayList<>();
                }
            })
            .shouldAddLogoutOptions()
            .shouldAddMainMenuOption();

        Menu.ADMIN_UPDATE_INVENTORY
            .setOptionGenerator(() -> {
                List<Option> options = new ArrayList<>(MedicationService.getAllMedications().stream()
                    .map(medication -> new Option(
                        String.join(" | ", 
                            "Medication ID: " + medication.getId(), 
                            "Name: " + medication.getName(), 
                            "Stock: " + medication.getStock(), 
                            "Low Alert Level: " + medication.getLowAlertLevel()
                        ), "edit " + medication.getName(), true
                        ).setNextMenu(() -> ADMIN_EDIT_MEDICATION)
                        .setNextAction((a, b) -> {
                            Map<String, Object> args = new HashMap<>();
                            args.put("medicationId", medication.getId());
                            return args;
                        })
                    )
                    .collect(Collectors.toList()));
                
                options.add(new Option(
                        "Add a medication (A)", 
                        "add( )?medication(s)?",
                        false
                    ).setNextMenu(() -> ADMIN_ADD_MEDICATION)
                );
                
                return options;
            })
            .shouldAddLogoutOptions().shouldAddMainMenuOption();

        Menu.ADMIN_ADD_MEDICATION.setNextMenu(ADMIN_ADD_INITIAL_STOCK)
            .setNextAction((userInput, args) -> {
                args.put("name", userInput);
                return args;
            });

        Menu.ADMIN_ADD_INITIAL_STOCK.setNextMenu(ADMIN_ADD_LOW_LEVEL_ALERT)
            .setNextAction((userInput, args) -> {
                args.put("stock", userInput);
                return args;
            });
        
        Menu.ADMIN_ADD_LOW_LEVEL_ALERT.setNextMenu(VIEW_INVENTORY)
            .setNextAction((userInput, args) -> {
                Medication p = Medication.create(
                    (String) args.get("name"),
                    (String) args.get("stock"),
                    userInput
                );
                return null;
            });
        
        Menu.ADMIN_EDIT_MEDICATION
            .setOptionGenerator(() -> {
                Map<String, Object> values = MenuService.getCurrentMenu().dataFromPreviousMenu;
                if (values != null && values.containsKey("medicationId")) {
                    Medication m = MedicationService.getMedication((int) values.get("medicationId"));
                    if (m == null) throw new Exception("Medication not found!");
                    return new ArrayList<>(List.of(
                        new EditOption(
                            String.format("Stock: %s", m.getStock()),
                            "update( )?(stock)?",
                            true,
                            (input, args) -> {
                                m.setStock(Integer.parseInt(input));
                                return null;
                            }
                        ),
                        new EditOption(
                            String.format("Low Level Alert: %s", m.getLowAlertLevel()),
                            "update( )?(level)?",
                            true,
                            (input, args) -> {
                                m.setLowAlertLevel(Integer.parseInt(input));
                                return null;
                            }
                        )
                    ));
                } else throw new Exception("Medication not found!");
            });
        
        Menu.ADMIN_VIEW_REQUEST.setDisplayGenerator(() -> {
            List<String> displayData = MedicationService.getAllMedications().stream()
                .flatMap(medication -> medication.getRequestList().stream()
                    .filter(request -> request.getStatus() == Request.Status.PENDING)
                    .map(request -> String.join(" | ",
                        "Request ID: " + request.getId(),
                        "Medication Name: " + medication.getName(),
                        "Medication ID: " + request.getMedicationId(),
                        "Quantity: " + request.getCount(),
                        "Status: " + request.getStatus()
                    ))
                )
                .collect(Collectors.toList());
        
            displayData.forEach(System.out::println);
        })
        .setOptionGenerator(() -> 
            new ArrayList<>(List.of(
                new Option(
                    "Approve a request", 
                    "approve( )?request(s)?",
                    true
                ).setNextMenu(() -> HANDLE_REPLENISH_REQUEST)
                .setNextAction((a, b) -> new HashMap<String, Object>() {{
                    Menu.HANDLE_REPLENISH_REQUEST.title = "Approve Request";
                    put("control", Control.APPROVE);
                }}),
                new Option(
                    "Reject a request", 
                    "reject( )?request(s)?",
                    true
                ).setNextMenu(() -> HANDLE_REPLENISH_REQUEST)
                .setNextAction((a, b) -> new HashMap<String, Object>() {{
                    Menu.HANDLE_REPLENISH_REQUEST.title = "Reject Request";
                    put("control", Control.REJECT);
                }})
            ))
        )
        .shouldAddLogoutOptions().shouldAddMainMenuOption();
        
        Menu.HANDLE_REPLENISH_REQUEST.setOptionGenerator(() -> {
            Map<String, Object> values = MenuService.getCurrentMenu().dataFromPreviousMenu;
            if (values == null || !values.containsKey("control")) throw new Exception();
            List<Option> options = MedicationService.getAllMedications().stream()
                .flatMap(medication -> medication.getRequestList().stream()
                    .filter(request -> request.getStatus() == Request.Status.PENDING)
                    .map(request -> new Option(
                        String.join(" | ", 
                            "Request ID: " + request.getId(), 
                            "Medication Name: " + medication.getName(), // Fixed: Added medication name
                            "Medication ID: " + request.getMedicationId(), 
                            "Quantity: " + request.getCount(), 
                            "Status: " + request.getStatus()
                        ),
                        "edit " + request.getId(),
                        true
                    ).setNextMenu(() -> ADMIN_VIEW_REQUEST)
                    .setNextAction((a, b) -> {
                        if (values.get("control") == Control.APPROVE) {
                            MedicationService.approveReplenishRequest(request);
                        } else {
                            request.setStatus(Request.Status.REJECTED);
                        }
                        return null;
                    }))
                )
                .collect(Collectors.toList());
                    
                return options;
                })
                .shouldAddLogoutOptions().shouldAddMainMenuOption();
    }
    // Init END

    // Helper START
    private enum MenuType {
        DISPLAY,
        INPUT,
        SELECT
    }

    private enum Control {
        ADD,
        EDIT,
        DELETE,
        APPROVE,
        REJECT
    }

    private static final Map<Class<?>, Menu> USER_MENU_MAP = Map.of(
        Patient.class, Menu.PATIENT_MAIN_MENU,
        Doctor.class, Menu.DOCTOR_MAIN_MENU,
        Pharmacist.class, Menu.PHARMACIST_MAIN_MENU,
        Admin.class, Menu.ADMIN_MAIN_MENU
    );

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
        private MenuGenerator nextMenuGenerator;
        private MenuGenerator exitMenuGenerator;
        protected NextAction nextAction = (input, args) -> args;
    
        public Option(
            String label,
            String matchPattern,
            boolean isNumberedOption
        ) {
            this.label = label;
            this.matchPattern = matchPattern;
            this.isNumberedOption = isNumberedOption;
        };

        protected MenuGenerator getNextMenuGenerator() {
            return this.nextMenuGenerator;
        }

        protected MenuGenerator getExitMenuGenerator() {
            return this.exitMenuGenerator;
        }

        protected NextAction getNextAction() {
            return this.nextAction;
        }
    
        protected final Option setNextMenu(Menu nextMenu) {
            return this.setNextMenu(() -> nextMenu);
        }

        protected Option setNextMenu(MenuGenerator nextMenuGenerator) {
            this.nextMenuGenerator = nextMenuGenerator;
            return this;
        }

        private Option setNextAction(NextAction nextAction) {
            this.nextAction = nextAction;
            return this;
        }
    }

    /**
     * The constructor expects a Next Action middleware, which gets called after selection to update the behaviour of the edit menu immediately before rendering it
     * subsequent calls to setNextAction will not work since setNextAction in Option is private
     * The Edit Option's next menu is then the Edit Menu
     * The Edit Option's error menu is then the 
     * The Edit Menus's error menu is itself, ie it will keep asking for user input 
     */
    private static class EditOption extends Option {

        public EditOption(
            String label,
            String matchPattern,
            boolean isNumberedOption,
            NextAction finalNextAction
        ) {
            super(label, matchPattern, isNumberedOption);
            Menu currentMenu = MenuService.getCurrentMenu();
            this.nextAction = ((userInput, args) -> {
                // edit menu updated
                Menu.EDIT
                    .setNextAction((innerInput, innerArgs) -> {
                        finalNextAction.apply(innerInput, innerArgs);
                        return innerArgs;
                    })
                    .setNextMenu(currentMenu);
                // return any args that might be required
                return args;
            });
            this.setNextMenu(Menu.EDIT); // Will always redirect to Edit Menu
        }
    }

    private static Option createRoleOption(Class<?> roleClass) {
        String className = roleClass.getSimpleName();
        
        return new Option(
            className,
            className.toLowerCase(),
            true
        ).setNextMenu(() -> ADMIN_ADD_USER_NAME)
        .setNextAction((input, args) -> new HashMap<String, Object>() {{
             put("role", className);
         }});
    }

    private static int parseUserIntInput(String userInput) throws Exception {
        try {
            return Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            throw new Exception("Please enter an integer/number.");
        }
    }

    private static Patient getTargetPatientFromArgs() throws Exception {
        Patient patient = (Patient) (
            MenuService.getCurrentMenu().dataFromPreviousMenu != null &&
            MenuService.getCurrentMenu().dataFromPreviousMenu.containsKey("patientId") ?
                UserService.findUserByIdAndType(
                    Integer.parseInt(
                        (String) MenuService.getCurrentMenu().dataFromPreviousMenu.get("patientId")
                    ),
                    Patient.class,
                    true
                ) : (
                    Patient.class.equals(UserService.getCurrentUser().getClass()) ?
                        UserService.getCurrentUser() : null
                )
        );
        if (patient == null) {
            throw new Exception("No patient found.");
        }
        return patient;
    }
    // Helper END




    // Builder START
    private static class MenuBuilder {

        private final MenuType menuType;
        private final String title;
        private final String label;
        private final boolean requiresConfirmation;
        
        MenuBuilder(MenuType menuType, String title, String label, boolean requiresConfirmation) {
            this.menuType = menuType;
            this.title = title;
            this.label = label;
            this.requiresConfirmation = requiresConfirmation;
        }

        MenuBuilder(MenuType menuType, String title, String label) {
            this(menuType, title, label, false);
        }
    }
    // Builder END

    private String title;
    private String label;
    private final MenuType menuType;
    private boolean parseUserInput = true;
    // Transitions & Actions START
    private MenuGenerator nextMenuGenerator;
    private MenuGenerator exitMenuGenerator;
    private NextAction nextAction = (input, args) -> args;
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
    private boolean requiresConfirmation;
    // Options END
        
    Menu(MenuBuilder menuBuilder) {
        this.menuType = menuBuilder.menuType;
        this.title = menuBuilder.title;
        this.label = menuBuilder.label;
        this.requiresConfirmation = menuBuilder.requiresConfirmation;
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
                throw new Error("Menu with type select should have at least one option");
            }
        }

        if (!(this.title == null || this.title.length() < 1)) {
            System.out.println(this.title);
            Menu.printLineBreak(50);
        }

        if (!(this.label == null || this.label.length() < 1)) {
            System.out.print("\n" + this.label + (
                this.menuType == MenuType.INPUT ? " " : "\n\n"
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
            if (this == Menu.CONFIRM) this.setDisplayGenerator(null);
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

    // BUG the exitMenu cannot be current menu
    private void setConfirmMenu(MenuGenerator nextMenu, MenuGenerator exitMenu, NextAction nextAction) {
        Menu.CONFIRM.setOptionGenerator(() -> Arrays.asList(
            new Option("Yes (Y)", "yes|y|yes( )?\\(?y\\)?", false)
                .setNextMenu(nextMenu).setNextAction(nextAction),
            new Option("No (N)", "no|n|no( )?\\(?n\\)?", false)
                .setNextMenu(exitMenu).setNextAction((input, args) -> args)
        )).setExitMenu(exitMenu);
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
            userInput = userInput.trim(); // Cannot lowercase since add user menus need true values, such as name, password etc 
        }

        System.out.printf("MenuType: %s%n", this.menuType); // IMPT DEBUG

        if (this.menuType == MenuType.INPUT && this.requiresConfirmation) {
            String trueInput = userInput;
            System.out.printf("Input Next Menu: %s%n, Input Exit Menu: %s%n", this.getNextMenu(), this.getExitMenu());
            this.setConfirmMenu(
                this.getNextMenuGenerator(),
                this.getExitMenuGenerator(),
                (input, args) -> {
                    return this.nextAction.apply(trueInput, args); // input MenuType.INPUT should have all nextAction() set, unlike MenuType.OPTION
                }
            );
            Menu.CONFIRM.setDataFromPreviousMenu(this.dataFromPreviousMenu);
            // this.setDataFromPreviousMenu(null);
            return Menu.CONFIRM;
        }

        if (this.menuType == MenuType.SELECT) {
            this.matchingOptions = (ArrayList<Option>) this.getFilteredOptions(userInput, true);
            List<Option> unNumberedMatches = this.getFilteredOptions(userInput, false);
            int numberOfMatches = unNumberedMatches.size() + this.matchingOptions.size();
            Option option = null;
            if (numberOfMatches < 1) {
                this.displayMode = DisplayMode.NO_MATCH_FOUND;
                System.out.println("No option match found"); // TODO REMOVE
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

            if (!this.requiresConfirmation || option == null) {
                // BUG if selection in confirm menu is not Y or N, then confirm menu is repeatedly shown
                if (option != null) {
                    this.setNextMenu(option.getNextMenuGenerator());
                    this.setNextAction(option.getNextAction());
                } else {
                    throw new Exception();
                }
            } else {
                // set next menu as the CONFIRM MENU
                this.setConfirmMenu(option.getNextMenuGenerator(), option.getExitMenuGenerator(), option.getNextAction());
                Menu.CONFIRM.setDataFromPreviousMenu(this.dataFromPreviousMenu);
                this.setDataFromPreviousMenu(null);
                return Menu.CONFIRM;
            }
        }
        
        System.out.printf("Before Executing Action: %s%n", this);
        Map<String, Object> argsForNext = this.setUserInput(userInput).executeNextAction(); // Options dont use user input since they are called within next action methods. Exceptions caught in here will propagate back to App.java which will render exitMenu
        System.out.println(argsForNext); // TODO: remove test

        System.out.printf("After Executing Action: %s%n", this.getNextMenu());
        Menu nextMenu = this.getNextMenu(); // IMPT only set data to null afterwards
        this.setDataFromPreviousMenu(null);
        return nextMenu.setDataFromPreviousMenu(argsForNext);
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

    public MenuGenerator getNextMenuGenerator() throws Exception {
        return this.nextMenuGenerator;
    }

    public MenuGenerator getExitMenuGenerator() throws Exception {
        return this.exitMenuGenerator == null ? () -> getUserMainMenu() : this.exitMenuGenerator;
    }

    public Menu getNextMenu() throws Exception { // Menu only
        return this.nextMenuGenerator.apply();
    }

    public Menu getExitMenu() throws Exception { // Menu only
        return this.getExitMenuGenerator().apply();
    }

    private Menu setNextMenu(Menu nextMenu) {
        return setNextMenu(() -> nextMenu);
    }

    private Menu setNextMenu(MenuGenerator nextMenuGenerator) {
        this.nextMenuGenerator = nextMenuGenerator;
        return this;
    }

    private Menu setExitMenu(MenuGenerator exitMenuGenerator) {
        this.exitMenuGenerator = exitMenuGenerator;
        return this;
    }

    public Menu setDataFromPreviousMenu(Map<String, Object> dataFromPreviousMenu) {
        this.dataFromPreviousMenu = dataFromPreviousMenu;
        return this;
    }

    private static Menu getUserMainMenu() {
        try {
            Class<?> userClass = UserService.getCurrentUser().getClass();
            return USER_MENU_MAP.getOrDefault(userClass, Menu.LANDING);  // Use a valid Menu default
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

    private Menu setPatientListOptionGenerator(MenuGenerator nextMenu) {
        return this.setOptionGenerator(() -> {
            List<Option> userOptions = UserService.getAllUserByType(Patient.class)
                .stream()
                .map(patient -> new Option(
                        String.format("%s (P%d)", patient.getName(), patient.getRoleId()),
                        String.format(
                            "%s|\\(?P?%d\\)?|%s( )?\\(?P?%d\\)?",
                            patient.getName(),
                            patient.getRoleId(),
                            patient.getName(),
                            patient.getRoleId()
                        ),
                        true
                    ).setNextMenu(nextMenu)
                    .setNextAction((userinput, args) -> new HashMap<String, Object>() {{
                        put("patientId", Integer.toString(patient.getRoleId()));
                    }})
                ).collect(Collectors.toList());
            if (userOptions != null && !userOptions.isEmpty()) {
                return userOptions;
            }
            MenuService.getCurrentMenu().setNextMenu(() -> Menu.getUserMainMenu());
            throw new Exception("No patients found.");
        });
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
    // Options handling - display & matching user input END
    // Options handling END
}
